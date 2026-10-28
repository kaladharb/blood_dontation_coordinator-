package com.bloodbank.servlet;

import com.bloodbank.dao.UserDao;
import com.bloodbank.model.User;
import com.bloodbank.util.HibernateUtil;
import com.bloodbank.util.PasswordUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

@WebServlet("/admin-setup")
public class AdminSetupServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (!isLocalRequest(request)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (userDao.hasAdmin(session)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            request.getRequestDispatcher("/WEB-INF/views/admin-setup.jsp").forward(request, response);
        } catch (RuntimeException exception) {
            throw new ServletException("Unable to open admin setup", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (!isLocalRequest(request)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String username = request.getParameter("username") == null
                ? "" : request.getParameter("username").trim();
        String password = request.getParameter("password");
        if (!username.matches("[A-Za-z0-9_]{3,50}") || password == null || password.length() < 8) {
            showError(request, response, "Use a 3-50 character username and a password of at least 8 characters.");
            return;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (userDao.hasAdmin(session)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (userDao.findByUsername(session, username) != null) {
                showError(request, response, "That username already exists. Choose another username.");
                return;
            }
            User admin = new User();
            admin.setUsername(username);
            admin.setPasswordHash(PasswordUtil.hash(password));
            admin.setRole("ADMIN");
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(admin);
                transaction.commit();
            } catch (RuntimeException exception) {
                if (transaction.isActive()) transaction.rollback();
                throw exception;
            }
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (RuntimeException exception) {
            throw new ServletException("Admin setup failed", exception);
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/WEB-INF/views/admin-setup.jsp").forward(request, response);
    }

    private boolean isLocalRequest(HttpServletRequest request) {
        String address = request.getRemoteAddr();
        return "127.0.0.1".equals(address) || "::1".equals(address) || "0:0:0:0:0:0:0:1".equals(address);
    }
}
