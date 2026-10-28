package com.bloodbank.servlet;

import com.bloodbank.dao.BloodDao;
import com.bloodbank.model.User;
import com.bloodbank.util.HibernateUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.hibernate.Session;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BloodDao dao = new BloodDao();
            request.setAttribute("inventory", dao.inventory(session));
            request.setAttribute("requests", dao.requestsByUser(session, user));
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
        } catch (RuntimeException exception) { throw new ServletException("Unable to load dashboard", exception); }
    }
}
