package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/updateProduct")
public class UpdateProductServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        int id;
        double price;
        int stock;

        try {

            id = Integer.parseInt(
                    request.getParameter("id")
            );

            price = Double.parseDouble(
                    request.getParameter("price")
            );

            stock = Integer.parseInt(
                    request.getParameter("stock")
            );

        } catch (Exception e) {

            response.setContentType("text/plain");

            response.getWriter().println(
                    "Invalid product data!"
            );

            return;
        }

        String name =
                request.getParameter("name");

        String description =
                request.getParameter("description");

        String category =
                request.getParameter("category");

        String imageUrl =
                request.getParameter("imageUrl");

        Product product =
                new Product(
                        name,
                        description,
                        price,
                        stock,
                        category,
                        imageUrl,
                        1
                );

        product.setId(id);

        ProductDAO productDAO =
                new ProductDAO();

        boolean success =
                productDAO.updateProduct(product);

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        if (success) {

            response.getWriter().println(
                    "<h2>Product updated successfully!</h2>"
            );

            response.getWriter().println(
                    "<a href='products'>View Products</a>"
            );

        } else {

            response.getWriter().println(
                    "<h2>Product update failed!</h2>"
            );
        }
    }
}