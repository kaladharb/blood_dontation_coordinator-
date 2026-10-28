package com.bloodbank.servlet;

import com.bloodbank.dao.BloodDao;
import com.bloodbank.model.*;
import com.bloodbank.util.HibernateUtil;
import java.io.IOException;
import jakarta.persistence.LockModeType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

@WebServlet({"/admin", "/admin/"})
public class AdminServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!guard(request, response)) return;
        if ("/admin/".equals(request.getServletPath())) {
            response.sendRedirect(dashboardUrl(request));
            return;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BloodDao dao = new BloodDao();
            request.setAttribute("inventory", dao.inventory(session));
            request.setAttribute("pending", dao.pendingRequests(session));
            request.setAttribute("flash", request.getSession().getAttribute("adminFlash"));
            request.getSession().removeAttribute("adminFlash");
            request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);
        } catch (RuntimeException exception) { throw new ServletException("Unable to load admin dashboard", exception); }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!guard(request, response)) return;
        String action = request.getParameter("action");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                if ("inventory".equals(action)) updateInventory(request, session);
                else if ("request".equals(action)) processRequest(request, session);
                else { tx.rollback(); response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid admin action"); return; }
                tx.commit();
            } catch (RuntimeException exception) {
                if (tx.isActive()) tx.rollback();
                throw exception;
            }
            response.sendRedirect(dashboardUrl(request));
        } catch (IllegalArgumentException exception) {
            request.getSession().setAttribute("adminFlash", exception.getMessage());
            response.sendRedirect(dashboardUrl(request));
        } catch (RuntimeException exception) {
            throw new ServletException("Admin operation failed", exception);
        }
    }
    private String dashboardUrl(HttpServletRequest request) {
        return request.getContextPath() + "/admin";
    }
    private void updateInventory(HttpServletRequest request, Session session) {
        String group = request.getParameter("bloodGroup"); int units = parse(request.getParameter("units"));
        if (group == null || !group.matches("^(A|B|AB|O)[+-]$") || units < 0 || units > 100000) throw new IllegalArgumentException("Invalid inventory values");
        BloodDao dao = new BloodDao(); BloodInventory item = dao.findInventory(session, group);
        if (item == null) { item = new BloodInventory(); item.setBloodGroup(group); }
        item.setUnits(units); session.saveOrUpdate(item);
    }
    private void processRequest(HttpServletRequest request, Session session) {
        long id = Long.parseLong(request.getParameter("requestId"));
        String decision = request.getParameter("decision");
        BloodRequest bloodRequest = session.find(BloodRequest.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (bloodRequest == null || !"PENDING".equals(bloodRequest.getStatus()) || (!"APPROVED".equals(decision) && !"REJECTED".equals(decision))) throw new IllegalArgumentException("Invalid request");
        if ("APPROVED".equals(decision)) {
            BloodDao dao = new BloodDao(); BloodInventory item = dao.findInventoryForUpdate(session, bloodRequest.getBloodGroup());
            if (item == null || item.getUnits() < bloodRequest.getUnits()) throw new IllegalArgumentException("Insufficient inventory");
            item.setUnits(item.getUnits() - bloodRequest.getUnits());
        }
        bloodRequest.setStatus(decision);
    }
    private boolean guard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) { response.sendRedirect(request.getContextPath() + "/login"); return false; }
        if (!AuthServlet.isAdmin(user)) { response.sendError(HttpServletResponse.SC_FORBIDDEN); return false; }
        return true;
    }
    private int parse(String value) { try { return Integer.parseInt(value); } catch (Exception exception) { return -1; } }
}
