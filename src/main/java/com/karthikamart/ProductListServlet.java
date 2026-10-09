package com.karthikamart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/products")
public class ProductListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        DatabaseInitializer.initialize();

        ProductDAO productDAO = new ProductDAO();
        List<Product> products = productDAO.getAllProducts();

        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().println("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>KarthikaMart Products</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background: #f5f5f5;
                        padding: 20px;
                    }
                    .product {
                        background: white;
                        border: 1px solid #ddd;
                        border-radius: 8px;
                        padding: 18px;
                        margin: 15px 0;
                        max-width: 450px;
                    }
                    button {
                        background: #ff9900;
                        color: white;
                        border: none;
                        padding: 10px 16px;
                        border-radius: 5px;
                        cursor: pointer;
                    }
                    input {
                        padding: 8px;
                        width: 65px;
                        margin-right: 8px;
                    }
                    a {
                        color: #d97700;
                    }
                </style>
            </head>
            <body>
                <h1>KarthikaMart</h1>
                <h2>Products</h2>
                <p><a href="index.html">Home</a> |
                   <a href="viewCart">View Cart</a></p>
            """);

        for (Product product : products) {

            if (product.getStock() <= 0) {
                continue;
            }

            response.getWriter().println(
                "<div class='product'>" +
                "<h3>" + escapeHtml(product.getName()) + "</h3>" +
                "<p>" + escapeHtml(product.getDescription()) + "</p>" +
                "<p>Price: ₹" + product.getPrice() + "</p>" +
                "<p>Category: " + escapeHtml(product.getCategory()) + "</p>" +
                "<p>Stock: " + product.getStock() + "</p>" +
                "<form action='addToCart' method='post'>" +
                "<input type='hidden' name='productId' value='" +
                    product.getId() + "'>" +
                "<label>Quantity: </label>" +
                "<input type='number' name='quantity' value='1' min='1' max='" +
                    product.getStock() + "' required>" +
                "<button type='submit'>Add to Cart</button>" +
                "</form>" +
                "</div>"
            );
        }

        response.getWriter().println("</body></html>");
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}