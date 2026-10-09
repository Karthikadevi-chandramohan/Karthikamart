package com.karthikamart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SellerOrderDAO {

    public void getSellerOrders(int sellerId) {

        String sql = """
            SELECT
                o.id AS order_id,
                o.user_id,
                o.total_amount,
                o.status,
                oi.product_id,
                oi.quantity,
                oi.price,
                p.name AS product_name
            FROM orders o
            JOIN order_items oi
                ON o.id = oi.order_id
            JOIN products p
                ON oi.product_id = p.id
            WHERE p.seller_id = ?
            ORDER BY o.id DESC
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, sellerId);

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                System.out.println(
                    "Order ID: " +
                    result.getInt("order_id")
                );

                System.out.println(
                    "Customer ID: " +
                    result.getInt("user_id")
                );

                System.out.println(
                    "Product: " +
                    result.getString("product_name")
                );

                System.out.println(
                    "Quantity: " +
                    result.getInt("quantity")
                );

                System.out.println(
                    "Price: ₹" +
                    result.getDouble("price")
                );

                System.out.println(
                    "Order Total: ₹" +
                    result.getDouble("total_amount")
                );

                System.out.println(
                    "Status: " +
                    result.getString("status")
                );

                System.out.println(
                    "-------------------------"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}