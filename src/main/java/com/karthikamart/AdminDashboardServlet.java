package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/admin")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
            !"ADMIN".equals(session.getAttribute("userRole"))) {

            response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Admin access only. Please login with an admin account."
            );
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().println(
            "<html><head><title>Admin Dashboard</title></head><body>"
        );
        response.getWriter().println("<h1>KarthikaMart</h1>");
        response.getWriter().println("<h2>Admin Dashboard</h2>");

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            int userCount = 0;
            int productCount = 0;
            int orderCount = 0;

            try (ResultSet users = statement.executeQuery(
                    "SELECT COUNT(*) FROM users")) {
                if (users.next()) userCount = users.getInt(1);
            }

            try (ResultSet products = statement.executeQuery(
                    "SELECT COUNT(*) FROM products")) {
                if (products.next()) productCount = products.getInt(1);
            }

            try (ResultSet orders = statement.executeQuery(
                    "SELECT COUNT(*) FROM orders")) {
                if (orders.next()) orderCount = orders.getInt(1);
            }

            response.getWriter().println("<p>Total Users: " + userCount + "</p>");
            response.getWriter().println("<p>Total Products: " + productCount + "</p>");
            response.getWriter().println("<p>Total Orders: " + orderCount + "</p>");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<p>Failed to load dashboard.</p>");
        }

        response.getWriter().println("</body></html>");
    }
}