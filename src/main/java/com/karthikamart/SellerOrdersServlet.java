package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/sellerOrders")
public class SellerOrdersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        int sellerId = 1;

        String sql = """
            SELECT
                o.id AS order_id,
                o.user_id,
                o.total_amount,
                o.status,
                oi.product_id,
                oi.quantity,
                oi.price,
                p.name AS product_name
            FROM orders o
            JOIN order_items oi
                ON o.id = oi.order_id
            JOIN products p
                ON oi.product_id = p.id
            WHERE p.seller_id = ?
            ORDER BY o.id DESC
            """;

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        response.getWriter().println(
                "<html><head><title>Seller Orders</title></head><body>"
        );

        response.getWriter().println(
                "<h1>KarthikaMart</h1>"
        );

        response.getWriter().println(
                "<h2>Customer Orders</h2>"
        );

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, sellerId);

            ResultSet result =
                    statement.executeQuery();

            boolean found = false;

            while (result.next()) {

                found = true;

                response.getWriter().println(
                    "<div style='border:1px solid #ccc;" +
                    "padding:15px;margin:10px;width:450px;'>" +

                    "<h3>Order ID: " +
                    result.getInt("order_id") +
                    "</h3>" +

                    "<p>Customer ID: " +
                    result.getInt("user_id") +
                    "</p>" +

                    "<p>Product: " +
                    result.getString("product_name") +
                    "</p>" +

                    "<p>Quantity: " +
                    result.getInt("quantity") +
                    "</p>" +

                    "<p>Product Price: ₹" +
                    result.getDouble("price") +
                    "</p>" +

                    "<p>Order Total: ₹" +
                    result.getDouble("total_amount") +
                    "</p>" +

                    "<p>Status: " +
                    result.getString("status") +
                    "</p>" +

                    "</div>"
                );
            }

            if (!found) {

                response.getWriter().println(
                    "<p>No customer orders found.</p>"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                "<p>Failed to load customer orders.</p>"
            );
        }

        response.getWriter().println(
                "</body></html>"
        );
    }
}