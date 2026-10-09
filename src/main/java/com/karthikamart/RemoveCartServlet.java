package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/removeCart")
public class RemoveCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        int cartId;

        try {

            cartId = Integer.parseInt(
                    request.getParameter("cartId")
            );

        } catch (Exception e) {

            response.setContentType("text/plain");

            response.getWriter().println(
                    "Invalid cart ID!"
            );

            return;
        }

        CartItemDAO cartItemDAO =
                new CartItemDAO();

        boolean success =
                cartItemDAO.removeCartItem(cartId);

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        if (success) {

            response.getWriter().println(
                    "<h2>Product removed from cart!</h2>"
            );

            response.getWriter().println(
                    "<a href='viewCart'>View Cart</a>"
            );

        } else {

            response.getWriter().println(
                    "<h2>Failed to remove product!</h2>"
            );
        }
    }
}