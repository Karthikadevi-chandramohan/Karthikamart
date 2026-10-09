package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        int userId = 1;

        CartItemDAO cartItemDAO = new CartItemDAO();
        ProductDAO productDAO = new ProductDAO();
        OrderDAO orderDAO = new OrderDAO();
        OrderItemDAO orderItemDAO = new OrderItemDAO();

        List<CartItem> cartItems =
                cartItemDAO.getCartItems(userId);

        List<Product> products =
                productDAO.getAllProducts();

        if (cartItems.isEmpty()) {

            response.setContentType("text/html;charset=UTF-8");

            response.getWriter().println(
                    "<h2>Your cart is empty!</h2>"
            );

            response.getWriter().println(
                    "<a href='products'>Continue Shopping</a>"
            );

            return;
        }

        double grandTotal = 0;

        for (CartItem item : cartItems) {

            for (Product product : products) {

                if (product.getId() == item.getProductId()) {

                    grandTotal +=
                            product.getPrice()
                            * item.getQuantity();
                }
            }
        }

        Order order =
                new Order(
                        userId,
                        grandTotal,
                        "PENDING"
                );

        int orderId =
                orderDAO.createOrder(order);

        if (orderId <= 0) {

            response.setContentType(
                    "text/html;charset=UTF-8"
            );

            response.getWriter().println(
                    "<h2>Order creation failed!</h2>"
            );

            return;
        }

        for (CartItem item : cartItems) {

            for (Product product : products) {

                if (product.getId() == item.getProductId()) {

                    double price =
                            product.getPrice();

                    OrderItem orderItem =
                            new OrderItem(
                                    orderId,
                                    product.getId(),
                                    item.getQuantity(),
                                    price
                            );

                    orderItemDAO.addOrderItem(
                            orderItem
                    );
                }
            }
        }

        for (CartItem item : cartItems) {

            cartItemDAO.removeCartItem(
                    item.getId()
            );
        }

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        response.getWriter().println(
                "<h1>KarthikaMart</h1>"
        );

        response.getWriter().println(
                "<h2>Order placed successfully!</h2>"
        );

        response.getWriter().println(
                "<p>Order ID: " +
                orderId +
                "</p>"
        );

        response.getWriter().println(
                "<p>Total Amount: ₹" +
                grandTotal +
                "</p>"
        );

        response.getWriter().println(
                "<p>Status: PENDING</p>"
        );

        response.getWriter().println(
                "<a href='myOrders'>View My Orders</a>"
        );
    }
}