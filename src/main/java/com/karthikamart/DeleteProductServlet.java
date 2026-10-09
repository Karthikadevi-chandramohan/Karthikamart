package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/deleteProduct")
public class DeleteProductServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        int productId;

        try {

            productId = Integer.parseInt(
                    request.getParameter("id")
            );

        } catch (Exception e) {

            response.setContentType("text/plain");

            response.getWriter().println(
                    "Invalid product ID!"
            );

            return;
        }

        ProductDAO productDAO =
                new ProductDAO();

        boolean success =
                productDAO.deleteProduct(productId);

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        if (success) {

            response.getWriter().println(
                    "<h2>Product deleted successfully!</h2>"
            );

            response.getWriter().println(
                    "<a href='products'>View Products</a>"
            );

        } else {

            response.getWriter().println(
                    "<h2>Product deletion failed!</h2>"
            );

            response.getWriter().println(
                    "<p>Check the Tomcat console for the database error.</p>"
            );

            response.getWriter().println(
                    "<a href='products'>Back to Products</a>"
            );
        }
    }
}