package com.karthikamart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public boolean addProduct(Product product) {

        String sql = """
            INSERT INTO products
            (name, description, price, stock, category, image_url, seller_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getStock());
            statement.setString(5, product.getCategory());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getSellerId());

            statement.executeUpdate();

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }


    public List<Product> getAllProducts() {

        List<Product> products =
                new ArrayList<>();

        String sql =
                "SELECT * FROM products";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result =
                     statement.executeQuery()) {

            while (result.next()) {

                Product product =
                        new Product();

                product.setId(
                        result.getInt("id")
                );

                product.setName(
                        result.getString("name")
                );

                product.setDescription(
                        result.getString("description")
                );

                product.setPrice(
                        result.getDouble("price")
                );

                product.setStock(
                        result.getInt("stock")
                );

                product.setCategory(
                        result.getString("category")
                );

                product.setImageUrl(
                        result.getString("image_url")
                );

                product.setSellerId(
                        result.getInt("seller_id")
                );

                products.add(product);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return products;
    }


    public boolean updateProduct(Product product) {

        String sql = """
            UPDATE products
            SET name = ?,
                description = ?,
                price = ?,
                stock = ?,
                category = ?,
                image_url = ?
            WHERE id = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    product.getName()
            );

            statement.setString(
                    2,
                    product.getDescription()
            );

            statement.setDouble(
                    3,
                    product.getPrice()
            );

            statement.setInt(
                    4,
                    product.getStock()
            );

            statement.setString(
                    5,
                    product.getCategory()
            );

            statement.setString(
                    6,
                    product.getImageUrl()
            );

            statement.setInt(
                    7,
                    product.getId()
            );

            int rows =
                    statement.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteProduct(int productId) {

        /*
         * Product may already be referenced by
         * orders or order_items.
         *
         * So we do not permanently delete it.
         * We set stock to 0 instead.
         */

        String sql = """
            UPDATE products
            SET stock = 0
            WHERE id = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    productId
            );

            int rows =
                    statement.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }
}