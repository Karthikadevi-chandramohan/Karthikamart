package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/updateCart")
public class UpdateCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        int cartId;
        int quantity;

        try {

            cartId = Integer.parseInt(
                    request.getParameter("cartId")
            );

            quantity = Integer.parseInt(
                    request.getParameter("quantity")
            );

        } catch (Exception e) {

            response.setContentType("text/plain");

            response.getWriter().println(
                    "Invalid cart ID or quantity!"
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

        CartItemDAO cartItemDAO =
                new CartItemDAO();

        boolean success =
                cartItemDAO.updateCartItem(
                        cartId,
                        quantity
                );

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        if (success) {

            response.getWriter().println(
                    "<h2>Cart updated successfully!</h2>"
            );

            response.getWriter().println(
                    "<a href='viewCart'>View Cart</a>"
            );

        } else {

            response.getWriter().println(
                    "<h2>Failed to update cart!</h2>"
            );
        }
    }
}