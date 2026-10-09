package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/addToCart")
public class AddToCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        int userId = 1;

        int productId;
        int quantity;

        try {
            productId = Integer.parseInt(
                    request.getParameter("productId")
            );

            quantity = Integer.parseInt(
                    request.getParameter("quantity")
            );

        } catch (Exception e) {

            response.setContentType("text/plain");
            response.getWriter().println(
                    "Invalid product or quantity!"
            );
            return;
        }

        if (quantity <= 0) {

            response.setContentType("text/plain");
            response.getWriter().println(
                    "Quantity must be greater than zero!"
            );
            return;
        }

        CartItem item =
                new CartItem(userId, productId, quantity);

        CartItemDAO cartItemDAO =
                new CartItemDAO();

        boolean success =
                cartItemDAO.addCartItem(item);

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        if (success) {

            response.getWriter().println(
                    "<h2>Product added to cart!</h2>"
            );

        } else {

            response.getWriter().println(
                    "<h2>Failed to add product to cart!</h2>"
            );
        }
    }
}