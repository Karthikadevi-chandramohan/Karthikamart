package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/addProduct")
public class ProductServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        DatabaseInitializer.initialize();

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        double price = Double.parseDouble(request.getParameter("price"));
        int stock = Integer.parseInt(request.getParameter("stock"));
        String category = request.getParameter("category");
        String imageUrl = request.getParameter("imageUrl");

        int sellerId = 1;

        Product product = new Product(
                name,
                description,
                price,
                stock,
                category,
                imageUrl,
                sellerId
        );

        ProductDAO productDAO = new ProductDAO();

        boolean success = productDAO.addProduct(product);

        response.setContentType("text/html;charset=UTF-8");

        if (success) {
            response.getWriter().println("Product added successfully!");
        } else {
            response.getWriter().println("Product addition failed!");
        }
    }
}