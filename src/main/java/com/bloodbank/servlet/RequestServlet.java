package com.bloodbank.servlet;

import com.bloodbank.model.*;
import com.bloodbank.util.HibernateUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

@WebServlet("/request-blood")
public class RequestServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
        String group = request.getParameter("bloodGroup");
        String reason = request.getParameter("reason");
        int units;
        try { units = Integer.parseInt(request.getParameter("units")); } catch (Exception exception) { units = 0; }
        if (!validGroup(group) || units < 1 || units > 100 || reason == null || reason.trim().length() < 5 || reason.length() > 500) {
            request.getSession().setAttribute("flash", "Enter a valid blood group, 1-100 units, and a reason of 5-500 characters.");
            response.sendRedirect(request.getContextPath() + "/dashboard"); return;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BloodRequest bloodRequest = new BloodRequest();
            bloodRequest.setRequester(user); bloodRequest.setBloodGroup(group); bloodRequest.setUnits(units); bloodRequest.setReason(reason.trim());
            Transaction tx = session.beginTransaction();
            try {
                session.persist(bloodRequest);
                tx.commit();
            } catch (RuntimeException exception) {
                if (tx.isActive()) tx.rollback();
                throw exception;
            }
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (RuntimeException exception) {
            throw new ServletException("Unable to create request", exception);
        }
    }
    private boolean validGroup(String group) { return group != null && group.matches("^(A|B|AB|O)[+-]$"); }
}
