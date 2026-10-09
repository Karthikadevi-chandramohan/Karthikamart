package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/viewCart")
public class ViewCartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        int userId = 1;

        CartItemDAO cartItemDAO = new CartItemDAO();
        ProductDAO productDAO = new ProductDAO();

        List<CartItem> items = cartItemDAO.getCartItems(userId);
        List<Product> products = productDAO.getAllProducts();

        double grandTotal = 0;

        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().println("<html>");
        response.getWriter().println("<head>");
        response.getWriter().println("<title>My Cart</title>");
        response.getWriter().println("</head>");
        response.getWriter().println("<body>");

        response.getWriter().println("<h1>KarthikaMart</h1>");
        response.getWriter().println("<h2>My Cart</h2>");

        if (items.isEmpty()) {

            response.getWriter().println(
                    "<p>Your cart is empty.</p>"
            );

        } else {

            for (CartItem item : items) {

                for (Product product : products) {

                    if (product.getId() == item.getProductId()) {

                        double total =
                                product.getPrice() * item.getQuantity();

                        grandTotal += total;

                        response.getWriter().println(
                            "<div style='border:1px solid #ccc;" +
                            "padding:15px;margin:10px;width:400px;'>" +

                            "<h3>" +
                            product.getName() +
                            "</h3>" +

                            "<p>Price: ₹" +
                            product.getPrice() +
                            "</p>" +

                            "<p>Quantity: " +
                            item.getQuantity() +
                            "</p>" +

                            "<p>Total: ₹" +
                            total +
                            "</p>" +

                            "<form action='updateCart' method='post'>" +

                            "<input type='hidden' " +
                            "name='cartId' " +
                            "value='" +
                            item.getId() +
                            "'>" +

                            "<input type='number' " +
                            "name='quantity' " +
                            "value='" +
                            item.getQuantity() +
                            "' min='1' required>" +

                            "<button type='submit'>" +
                            "Update" +
                            "</button>" +

                            "</form>" +

                            "<br>" +

                            "<form action='removeCart' method='post'>" +

                            "<input type='hidden' " +
                            "name='cartId' " +
                            "value='" +
                            item.getId() +
                            "'>" +

                            "<button type='submit'>" +
                            "Remove" +
                            "</button>" +

                            "</form>" +

                            "</div>"
                        );
                    }
                }
            }

            response.getWriter().println(
                    "<h2>Grand Total: ₹" +
                    grandTotal +
                    "</h2>"
            );
            response.getWriter().println(
                    "<form action='checkout' method='post'>" +
                    "<button type='submit'>Proceed to Checkout</button>" +
                    "</form>"
            );
        }

        response.getWriter().println("</body>");
        response.getWriter().println("</html>");
    }
}