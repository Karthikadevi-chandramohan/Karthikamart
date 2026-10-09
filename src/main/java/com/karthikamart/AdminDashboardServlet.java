package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().println(
            "<html><head><title>Admin Dashboard</title></head><body>"
        );

        response.getWriter().println("<h1>KarthikaMart</h1>");
        response.getWriter().println("<h2>Admin Dashboard</h2>");

        try (Connection connection =
                     DatabaseConnection.getConnection();
             Statement statement =
                     connection.createStatement()) {

            ResultSet users =
                    statement.executeQuery(
                        "SELECT COUNT(*) FROM users"
                    );

            users.next();
            int userCount = users.getInt(1);

            ResultSet products =
                    statement.executeQuery(
                        "SELECT COUNT(*) FROM products"
                    );

            products.next();
            int productCount = products.getInt(1);

            ResultSet orders =
                    statement.executeQuery(
                        "SELECT COUNT(*) FROM orders"
                    );

            orders.next();
            int orderCount = orders.getInt(1);

            response.getWriter().println(
                "<p>Total Users: " + userCount + "</p>"
            );

            response.getWriter().println(
                "<p>Total Products: " + productCount + "</p>"
            );

            response.getWriter().println(
                "<p>Total Orders: " + orderCount + "</p>"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                "<p>Failed to load dashboard.</p>"
            );
        }

        response.getWriter().println("</body></html>");
    }
}
