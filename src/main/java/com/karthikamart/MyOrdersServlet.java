package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/myOrders")
public class MyOrdersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        int userId = 1;

        OrderDAO orderDAO = new OrderDAO();

        List<Order> orders =
                orderDAO.getOrdersByUser(userId);

        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().println("<html>");
        response.getWriter().println("<head>");
        response.getWriter().println("<title>My Orders</title>");
        response.getWriter().println("</head>");
        response.getWriter().println("<body>");

        response.getWriter().println("<h1>KarthikaMart</h1>");
        response.getWriter().println("<h2>My Orders</h2>");

        if (orders.isEmpty()) {

            response.getWriter().println(
                    "<p>No orders found.</p>"
            );

        } else {

            for (Order order : orders) {

                response.getWriter().println(
                    "<div style='border:1px solid #ccc;" +
                    "padding:15px;margin:10px;width:300px;'>" +

                    "<h3>Order #" +
                    order.getId() +
                    "</h3>" +

                    "<p>Total: ₹" +
                    order.getTotalAmount() +
                    "</p>" +

                    "<p>Status: " +
                    order.getStatus() +
                    "</p>" +

                    "</div>"
                );
            }
        }

        response.getWriter().println("</body>");
        response.getWriter().println("</html>");
    }
}