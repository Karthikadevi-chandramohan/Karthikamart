package com.karthikamart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CartItemDAO {

    public boolean addCartItem(CartItem item) {

        String checkSql = """
            SELECT id, quantity
            FROM cart_items
            WHERE user_id = ? AND product_id = ?
            """;

        String insertSql = """
            INSERT INTO cart_items
            (user_id, product_id, quantity)
            VALUES (?, ?, ?)
            """;

        String updateSql = """
            UPDATE cart_items
            SET quantity = ?
            WHERE id = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            try (PreparedStatement check =
                         connection.prepareStatement(checkSql)) {

                check.setInt(1, item.getUserId());
                check.setInt(2, item.getProductId());

                ResultSet result = check.executeQuery();

                if (result.next()) {

                    int cartId = result.getInt("id");
                    int oldQuantity = result.getInt("quantity");

                    try (PreparedStatement update =
                                 connection.prepareStatement(updateSql)) {

                        update.setInt(
                                1,
                                oldQuantity + item.getQuantity()
                        );

                        update.setInt(2, cartId);

                        update.executeUpdate();

                        return true;
                    }

                } else {

                    try (PreparedStatement insert =
                                 connection.prepareStatement(insertSql)) {

                        insert.setInt(1, item.getUserId());
                        insert.setInt(2, item.getProductId());
                        insert.setInt(3, item.getQuantity());

                        insert.executeUpdate();

                        return true;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<CartItem> getCartItems(int userId) {

        List<CartItem> items = new ArrayList<>();

        String sql = """
            SELECT id, user_id, product_id, quantity
            FROM cart_items
            WHERE user_id = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                CartItem item = new CartItem();

                item.setId(result.getInt("id"));
                item.setUserId(result.getInt("user_id"));
                item.setProductId(result.getInt("product_id"));
                item.setQuantity(result.getInt("quantity"));

                items.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }


    public boolean updateCartItem(int cartId, int quantity) {

        String sql = """
            UPDATE cart_items
            SET quantity = ?
            WHERE id = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setInt(2, cartId);

            statement.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean removeCartItem(int cartId) {

        String sql = """
            DELETE FROM cart_items
            WHERE id = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, cartId);

            statement.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}