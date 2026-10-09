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

@WebServlet("/search")
public class ProductSearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String search = request.getParameter("search");
        String category = request.getParameter("category");

        if (search == null) search = "";
        if (category == null) category = "";

        String sql = """
            SELECT * FROM products
            WHERE stock > 0
            AND name LIKE ?
            AND category LIKE ?
            """;

        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().println("<h1>KarthikaMart</h1>");
        response.getWriter().println("<h2>Search Products</h2>");

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, "%" + search + "%");
            statement.setString(2, "%" + category + "%");

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                response.getWriter().println(
                    "<div style='border:1px solid #ccc;" +
                    "padding:10px;margin:10px;width:400px;'>" +

                    "<h3>" +
                    result.getString("name") +
                    "</h3>" +

                    "<p>Price: ₹" +
                    result.getDouble("price") +
                    "</p>" +

                    "<p>Category: " +
                    result.getString("category") +
                    "</p>" +

                    "<p>Stock: " +
                    result.getInt("stock") +
                    "</p>" +

                    "</div>"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println(
                "<p>Search failed!</p>"
            );
        }
    }
}