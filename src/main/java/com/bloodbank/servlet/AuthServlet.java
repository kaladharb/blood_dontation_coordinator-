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

@WebServlet({"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if ("/logout".equals(request.getServletPath())) {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String path = request.getServletPath();
        if ("/register".equals(path)) register(request, response);
        else login(request, response);
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = clean(request.getParameter("username"));
        String password = request.getParameter("password");
        if (username.isEmpty() || password == null || password.length() < 6) {
            showError(request, response, "Enter a username and a password of at least 6 characters."); return;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = userDao.findByUsername(session, username);
            if (user == null || !PasswordUtil.matches(password, user.getPasswordHash())) {
                showError(request, response, "Invalid username or password."); return;
            }
            request.getSession(true);
            request.changeSessionId();
            request.getSession().setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + (isAdmin(user) ? "/admin" : "/dashboard"));
        } catch (RuntimeException exception) {
            throw new ServletException("Login failed", exception);
        }
    }

    private void register(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = clean(request.getParameter("username"));
        String password = request.getParameter("password");
        if (!username.matches("[A-Za-z0-9_]{3,50}") || password == null || password.length() < 6) {
            showError(request, response, "Username must be 3-50 letters/numbers/underscore and password at least 6 characters."); return;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (userDao.findByUsername(session, username) != null) {
                showError(request, response, "Username already exists."); return;
            }
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(PasswordUtil.hash(password));
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
            } catch (RuntimeException exception) {
                if (transaction.isActive()) transaction.rollback();
                throw exception;
            }
            request.setAttribute("message", "Registration successful. Please log in.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } catch (RuntimeException exception) {
            throw new ServletException("Registration failed", exception);
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
    private static String clean(String value) { return value == null ? "" : value.trim(); }
    public static boolean isAdmin(User user) { return user != null && "ADMIN".equals(user.getRole()); }
}
