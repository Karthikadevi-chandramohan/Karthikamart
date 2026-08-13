package com.karthika.laptopmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class KarthikaLaptopMartApplication {

    private final List<User> users = new ArrayList<>();
    private final List<Laptop> laptops = new ArrayList<>();
    private final List<CartItem> cart = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();
    private final List<WishlistItem> wishlist = new ArrayList<>();

    private final AtomicInteger userId = new AtomicInteger(1);
    private final AtomicInteger laptopId = new AtomicInteger(1);
    private final AtomicInteger orderId = new AtomicInteger(1001);
    private final AtomicInteger reviewId = new AtomicInteger(1);

    public KarthikaLaptopMartApplication() {

        users.add(new User(
                userId.getAndIncrement(),
                "Karthika",
                "karthika@gmail.com",
                "123456",
                "BUYER"
        ));

        users.add(new User(
                userId.getAndIncrement(),
                "Laptop Seller",
                "seller@karthika.com",
                "123456",
                "SELLER"
        ));

        addSampleLaptop(
                "Dell Inspiron 15",
                "Intel Core i5 laptop with 8GB RAM",
                "Dell",
                "Electronics",
                55000,
                20
        );

        addSampleLaptop(
                "HP Pavilion 14",
                "Intel Core i5 laptop with 16GB RAM",
                "HP",
                "Electronics",
                62000,
                15
        );

        addSampleLaptop(
                "Lenovo IdeaPad Slim 3",
                "AMD Ryzen laptop for students",
                "Lenovo",
                "Electronics",
                48000,
                25
        );

        addSampleLaptop(
                "Acer Aspire 5",
                "Powerful laptop for programming",
                "Acer",
                "Electronics",
                52000,
                18
        );

        addSampleLaptop(
                "ASUS VivoBook 15",
                "Lightweight laptop for students",
                "ASUS",
                "Electronics",
                58000,
                12
        );

        addSampleLaptop(
                "MacBook Air M2",
                "Apple MacBook with M2 chip",
                "Apple",
                "Premium Laptops",
                95000,
                10
        );
    }

    private void addSampleLaptop(
            String name,
            String description,
            String brand,
            String category,
            double price,
            int stock) {

        laptops.add(
                new Laptop(
                        laptopId.getAndIncrement(),
                        name,
                        description,
                        brand,
                        category,
                        price,
                        stock
                )
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(
                KarthikaLaptopMartApplication.class,
                args
        );
    }

    // ================= HOME =================

    @GetMapping("/")
    public String home() {
        return "Welcome to Karthika LaptopMart";
    }

    // ================= REGISTER =================

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        if (request.name == null ||
                request.email == null ||
                request.password == null) {

            return ResponseEntity.badRequest()
                    .body("All fields are required");
        }

        for (User user : users) {

            if (user.email.equalsIgnoreCase(
                    request.email)) {

                return ResponseEntity.badRequest()
                        .body("Email already registered");
            }
        }

        User user = new User(
                userId.getAndIncrement(),
                request.name,
                request.email,
                request.password,
                "BUYER"
        );

        users.add(user);

        return ResponseEntity.ok(
                "Registration successful"
        );
    }

    // ================= LOGIN =================

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        for (User user : users) {

            if (user.email.equalsIgnoreCase(
                    request.email)
                    &&
                    user.password.equals(
                            request.password)) {

                Map<String, Object> response =
                        new HashMap<>();

                response.put(
                        "message",
                        "Login successful"
                );

                response.put(
                        "userId",
                        user.id
                );

                response.put(
                        "name",
                        user.name
                );

                response.put(
                        "role",
                        user.role
                );

                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
    }

    // ================= ALL LAPTOPS =================

    @GetMapping("/laptops")
    public List<Laptop> getAllLaptops() {
        return laptops;
    }

    // ================= LAPTOP BY ID =================

    @GetMapping("/laptops/{id}")
    public ResponseEntity<?> getLaptop(
            @PathVariable int id) {

        for (Laptop laptop : laptops) {

            if (laptop.id == id) {
                return ResponseEntity.ok(laptop);
            }
        }

        return ResponseEntity.notFound().build();
    }

    // ================= SEARCH =================

    @GetMapping("/laptops/search")
    public List<Laptop> searchLaptop(
            @RequestParam String keyword) {

        List<Laptop> result =
                new ArrayList<>();

        for (Laptop laptop : laptops) {

            if (laptop.name
                    .toLowerCase()
                    .contains(keyword.toLowerCase())
                    ||
                    laptop.brand
                            .toLowerCase()
                            .contains(keyword.toLowerCase())
                    ||
                    laptop.description
                            .toLowerCase()
                            .contains(keyword.toLowerCase())) {

                result.add(laptop);
            }
        }

        return result;
    }

    // ================= BRAND FILTER =================

    @GetMapping("/laptops/brand/{brand}")
    public List<Laptop> brandFilter(
            @PathVariable String brand) {

        List<Laptop> result =
                new ArrayList<>();

        for (Laptop laptop : laptops) {

            if (laptop.brand
                    .equalsIgnoreCase(brand)) {

                result.add(laptop);
            }
        }

        return result;
    }

    // ================= CATEGORY FILTER =================

    @GetMapping("/laptops/category/{category}")
    public List<Laptop> categoryFilter(
            @PathVariable String category) {

        List<Laptop> result =
                new ArrayList<>();

        for (Laptop laptop : laptops) {

            if (laptop.category
                    .equalsIgnoreCase(category)) {

                result.add(laptop);
            }
        }

        return result;
    }

    // ================= SELLER ADD LAPTOP =================

    @PostMapping("/seller/laptops")
    public ResponseEntity<?> addLaptop(
            @RequestBody Laptop laptop) {

        Laptop newLaptop =
                new Laptop(
                        laptopId.getAndIncrement(),
                        laptop.name,
                        laptop.description,
                        laptop.brand,
                        laptop.category,
                        laptop.price,
                        laptop.stock
                );

        laptops.add(newLaptop);

        return ResponseEntity.ok(
                "Laptop added successfully"
        );
    }

    // ================= SELLER UPDATE =================

    @PutMapping("/seller/laptops/{id}")
    public ResponseEntity<?> updateLaptop(
            @PathVariable int id,
            @RequestBody Laptop updatedLaptop) {

        for (int i = 0; i < laptops.size(); i++) {

            if (laptops.get(i).id == id) {

                Laptop laptop =
                        new Laptop(
                                id,
                                updatedLaptop.name,
                                updatedLaptop.description,
                                updatedLaptop.brand,
                                updatedLaptop.category,
                                updatedLaptop.price,
                                updatedLaptop.stock
                        );

                laptops.set(i, laptop);

                return ResponseEntity.ok(
                        "Laptop updated successfully"
                );
            }
        }

        return ResponseEntity.notFound().build();
    }

    // ================= SELLER DELETE =================

    @DeleteMapping("/seller/laptops/{id}")
    public ResponseEntity<?> deleteLaptop(
            @PathVariable int id) {

        boolean removed =
                laptops.removeIf(
                        laptop -> laptop.id == id
                );

        if (removed) {
            return ResponseEntity.ok(
                    "Laptop deleted successfully"
            );
        }

        return ResponseEntity.notFound()
                .build();
    }

    // ================= ADD TO CART =================

    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(
            @RequestBody CartRequest request) {

        Laptop laptop =
                findLaptop(request.laptopId);

        if (laptop == null) {

            return ResponseEntity.badRequest()
                    .body("Laptop not found");
        }

        if (request.quantity <= 0) {

            return ResponseEntity.badRequest()
                    .body("Invalid quantity");
        }

        if (request.quantity > laptop.stock) {

            return ResponseEntity.badRequest()
                    .body("Not enough stock");
        }

        for (CartItem item : cart) {

            if (item.userId == request.userId
                    &&
                    item.laptopId ==
                            request.laptopId) {

                item.quantity +=
                        request.quantity;

                return ResponseEntity.ok(
                        "Cart quantity updated"
                );
            }
        }

        cart.add(
                new CartItem(
                        request.userId,
                        request.laptopId,
                        request.quantity
                )
        );

        return ResponseEntity.ok(
                "Laptop added to cart"
        );
    }

    // ================= VIEW CART =================

    @GetMapping("/cart/{userId}")
    public List<Map<String, Object>> viewCart(
            @PathVariable int userId) {

        List<Map<String, Object>> result =
                new ArrayList<>();

        for (CartItem item : cart) {

            if (item.userId == userId) {

                Laptop laptop =
                        findLaptop(item.laptopId);

                if (laptop != null) {

                    Map<String, Object> data =
                            new HashMap<>();

                    data.put(
                            "laptopId",
                            laptop.id
                    );

                    data.put(
                            "name",
                            laptop.name
                    );

                    data.put(
                            "brand",
                            laptop.brand
                    );

                    data.put(
                            "price",
                            laptop.price
                    );

                    data.put(
                            "quantity",
                            item.quantity
                    );

                    data.put(
                            "subtotal",
                            laptop.price *
                                    item.quantity
                    );

                    result.add(data);
                }
            }
        }

        return result;
    }

    // ================= UPDATE CART =================

    @PutMapping("/cart/update")
    public ResponseEntity<?> updateCart(
            @RequestBody CartRequest request) {

        for (CartItem item : cart) {

            if (item.userId == request.userId
                    &&
                    item.laptopId ==
                            request.laptopId) {

                Laptop laptop =
                        findLaptop(
                                request.laptopId
                        );

                if (laptop == null) {

                    return ResponseEntity.badRequest()
                            .body(
                                    "Laptop not found"
                            );
                }

                if (request.quantity >
                        laptop.stock) {

                    return ResponseEntity.badRequest()
                            .body(
                                    "Not enough stock"
                            );
                }

                item.quantity =
                        request.quantity;

                return ResponseEntity.ok(
                        "Cart updated"
                );
            }
        }

        return ResponseEntity.notFound()
                .build();
    }

    // ================= REMOVE CART =================

    @DeleteMapping("/cart/remove")
    public ResponseEntity<?> removeCart(
            @RequestParam int userId,
            @RequestParam int laptopId) {

        boolean removed =
                cart.removeIf(
                        item ->
                                item.userId == userId
                                        &&
                                item.laptopId ==
                                        laptopId
                );

        if (removed) {

            return ResponseEntity.ok(
                    "Laptop removed from cart"
            );
        }

        return ResponseEntity.notFound()
                .build();
    }

    // ================= CHECKOUT =================

    @PostMapping("/orders/checkout/{userId}")
    public ResponseEntity<?> checkout(
            @PathVariable int userId) {

        List<CartItem> userCart =
                new ArrayList<>();

        for (CartItem item : cart) {

            if (item.userId == userId) {
                userCart.add(item);
            }
        }

        if (userCart.isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Cart is empty");
        }

        double total = 0;

        List<OrderItem> orderItems =
                new ArrayList<>();

        for (CartItem item : userCart) {

            Laptop laptop =
                    findLaptop(
                            item.laptopId
                    );

            if (laptop == null) {
                continue;
            }

            if (item.quantity >
                    laptop.stock) {

                return ResponseEntity.badRequest()
                        .body(
                                "Not enough stock for "
                                        + laptop.name
                        );
            }

            total +=
                    laptop.price *
                            item.quantity;

            orderItems.add(
                    new OrderItem(
                            laptop.id,
                            laptop.name,
                            item.quantity,
                            laptop.price
                    )
            );
        }

        for (CartItem item : userCart) {

            Laptop laptop =
                    findLaptop(
                            item.laptopId
                    );

            if (laptop != null) {

                laptop.stock -=
                        item.quantity;
            }
        }

        Order order =
                new Order(
                        orderId.getAndIncrement(),
                        userId,
                        orderItems,
                        total,
                        "PENDING",
                        LocalDateTime.now()
                );

        orders.add(order);

        cart.removeIf(
                item -> item.userId == userId
        );

        return ResponseEntity.ok(order);
    }

    // ================= ORDER HISTORY =================

    @GetMapping("/orders/{userId}")
    public List<Order> orderHistory(
            @PathVariable int userId) {

        List<Order> result =
                new ArrayList<>();

        for (Order order : orders) {

            if (order.userId == userId) {
                result.add(order);
            }
        }

        return result;
    }

    // ================= ORDER TRACKING =================

    @GetMapping(
            "/orders/{userId}/{orderId}"
    )
    public ResponseEntity<?> trackOrder(
            @PathVariable int userId,
            @PathVariable int orderId) {

        for (Order order : orders) {

            if (order.id == orderId
                    &&
                    order.userId == userId) {

                return ResponseEntity.ok(
                        order
                );
            }
        }

        return ResponseEntity.notFound()
                .build();
    }

    // ================= REVIEW =================

    @PostMapping("/reviews")
    public ResponseEntity<?> addReview(
            @RequestBody Review review) {

        Laptop laptop =
                findLaptop(review.laptopId);

        if (laptop == null) {

            return ResponseEntity.badRequest()
                    .body(
                            "Laptop not found"
                    );
        }

        if (review.rating < 1 ||
                review.rating > 5) {

            return ResponseEntity.badRequest()
                    .body(
                            "Rating must be between 1 and 5"
                    );
        }

        Review newReview =
                new Review(
                        reviewId.getAndIncrement(),
                        review.userId,
                        review.laptopId,
                        review.rating,
                        review.comment
                );

        reviews.add(newReview);

        return ResponseEntity.ok(
                "Review submitted successfully"
        );
    }

    // ================= GET REVIEWS =================

    @GetMapping("/reviews/{laptopId}")
    public List<Review> getReviews(
            @PathVariable int laptopId) {

        List<Review> result =
                new ArrayList<>();

        for (Review review : reviews) {

        if (review.laptopId == laptopId) {
            result.add(review);
        }
    }

    return result;
}

// ================= WISHLIST =================

@PostMapping("/wishlist/add")
public String addWishlist(
        @RequestBody WishlistItem item) {

    wishlist.add(item);

    return "Laptop added to wishlist";
}

@GetMapping("/wishlist/{userId}")
public List<Laptop> getWishlist(
        @PathVariable int userId) {

    List<Laptop> result =
            new ArrayList<>();

    for (WishlistItem item : wishlist) {

        if (item.userId == userId) {

            Laptop laptop =
                    findLaptop(item.laptopId);

            if (laptop != null) {
                result.add(laptop);
            }
        }
    }

    return result;
}

// ================= SELLER ORDERS =================

@GetMapping("/seller/orders")
public List<Order> sellerOrders() {
    return orders;
}

// ================= SALES DASHBOARD =================

@GetMapping("/seller/dashboard")
public Map<String, Object> sellerDashboard() {

    double totalSales = 0;

    for (Order order : orders) {
        totalSales += order.total;
    }

    Map<String, Object> dashboard =
            new HashMap<>();

    dashboard.put("totalOrders", orders.size());
    dashboard.put("totalSales", totalSales);
    dashboard.put("availableLaptops", laptops.size());

    return dashboard;
}

// ================= CHATBOT =================

@GetMapping("/chatbot")
public String chatbot(
        @RequestParam String message) {

    String text = message.toLowerCase();

    if (text.contains("dell")) {
        return "Dell laptops are available starting from ₹55000.";
    }

    if (text.contains("hp")) {
        return "HP Pavilion 14 is available at ₹62000.";
    }

    if (text.contains("lenovo")) {
        return "Lenovo IdeaPad Slim 3 is available at ₹48000.";
    }

    if (text.contains("acer")) {
        return "Acer Aspire 5 is available at ₹52000.";
    }

    if (text.contains("asus")) {
        return "ASUS VivoBook 15 is available at ₹58000.";
    }

    if (text.contains("macbook")
            || text.contains("apple")) {
        return "MacBook Air M2 is available at ₹95000.";
    }

    if (text.contains("order")) {
        return "You can track your laptop order using the Order Tracking API.";
    }

    if (text.contains("cart")) {
        return "You can add laptops to your cart and proceed to checkout.";
    }

    if (text.contains("recommend")) {
        return "For students, I recommend Lenovo IdeaPad Slim 3 or Acer Aspire 5.";
    }

    if (text.contains("programming")) {
        return "For programming, Acer Aspire 5 and Dell Inspiron 15 are good choices.";
    }

    return "Welcome to Karthika LaptopMart. How can I help you?";
}

// ================= FIND LAPTOP =================

private Laptop findLaptop(int id) {

    for (Laptop laptop : laptops) {

        if (laptop.id == id) {
            return laptop;
        }
    }

    return null;
}

// ================= USER MODEL =================

static class User {

    int id;
    String name;
    String email;
    String password;
    String role;

    User(
            int id,
            String name,
            String email,
            String password,
            String role) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}

// ================= LAPTOP MODEL =================

static class Laptop {

    int id;
    String name;
    String description;
    String brand;
    String category;
    double price;
    int stock;

    Laptop(
            int id,
            String name,
            String description,
            String brand,
            String category,
            double price,
            int stock) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }
}

// ================= CART MODEL =================

static class CartItem {

    int userId;
    int laptopId;
    int quantity;

    CartItem(
            int userId,
            int laptopId,
            int quantity) {

        this.userId = userId;
        this.laptopId = laptopId;
        this.quantity = quantity;
    }
}

// ================= ORDER MODEL =================

static class Order {

    int id;
    int userId;
    List<OrderItem> items;
    double total;
    String status;
    LocalDateTime date;

    Order(
            int id,
            int userId,
            List<OrderItem> items,
            double total,
            String status,
            LocalDateTime date) {

        this.id = id;
        this.userId = userId;
        this.items = items;
        this.total = total;
        this.status = status;
        this.date = date;
    }
}

// ================= ORDER ITEM =================

static class OrderItem {

    int laptopId;
    String laptopName;
    int quantity;
    double price;

    OrderItem(
            int laptopId,
            String laptopName,
            int quantity,
            double price) {

        this.laptopId = laptopId;
        this.laptopName = laptopName;
        this.quantity = quantity;
        this.price = price;
    }
}

// ================= REVIEW MODEL =================

static class Review {

    int id;
    int userId;
    int laptopId;
    int rating;
    String comment;

    Review(
            int id,
            int userId,
            int laptopId,
            int rating,
            String comment) {

        this.id = id;
        this.userId = userId;
        this.laptopId = laptopId;
        this.rating = rating;
        this.comment = comment;
    }
}

// ================= WISHLIST =================

static class WishlistItem {

    int userId;
    int laptopId;

    WishlistItem(
            int userId,
            int laptopId) {

        this.userId = userId;
        this.laptopId = laptopId;
    }
}

// ================= REGISTER REQUEST =================

static class RegisterRequest {

    String name;
    String email;
    String password;
}

// ================= LOGIN REQUEST =================

static class LoginRequest {

    String email;
    String password;
}

// ================= CART REQUEST =================

static class CartRequest {

    int userId;
    int laptopId;
    int quantity;
}
// ================= ADMIN DASHBOARD =================

    @GetMapping("/admin/dashboard")
    public Map<String, Object> adminDashboard() {

        Map<String, Object> dashboard =
                new LinkedHashMap<>();

        dashboard.put("totalUsers", users.size());
        dashboard.put("totalLaptops", laptops.size());
        dashboard.put("totalOrders", orders.size());
        dashboard.put("totalReviews", reviews.size());
        dashboard.put("totalWishlistItems", wishlist.size());

        double totalSales = 0;

        for (Order order : orders) {
            totalSales += order.total;
        }

        dashboard.put("totalSales", totalSales);

        return dashboard;
    }

    // ================= ADMIN USERS =================

    @GetMapping("/admin/users")
    public List<User> getAllUsers() {
        return users;
    }

    // ================= ADMIN DELETE USER =================

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable int id) {

        User user = findUser(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        users.remove(user);

        return ResponseEntity.ok(
                "User deleted successfully"
        );
    }

    // ================= ADMIN ALL ORDERS =================

    @GetMapping("/admin/orders")
    public List<Order> getAllOrders() {
        return orders;
    }

    // ================= ADMIN UPDATE ORDER STATUS =================

    @PutMapping("/admin/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable int orderId,
            @RequestParam String status) {

        for (Order order : orders) {

            if (order.id == orderId) {

                order.status =
                        status.toUpperCase();

                return ResponseEntity.ok(
                        "Order status updated to "
                                + order.status
                );
            }
        }

        return ResponseEntity.notFound()
                .build();
    }

    // ================= ADMIN REMOVE PRODUCT =================

    @DeleteMapping("/admin/laptops/{id}")
    public ResponseEntity<?> adminDeleteLaptop(
            @PathVariable int id) {

        Laptop laptop = findLaptop(id);

        if (laptop == null) {

            return ResponseEntity.notFound()
                    .build();
        }

        laptops.remove(laptop);

        return ResponseEntity.ok(
                "Laptop removed by admin"
        );
    }

    // ================= ADMIN ALL REVIEWS =================

    @GetMapping("/admin/reviews")
    public List<Review> getAllReviews() {
        return reviews;
    }

    // ================= ADMIN DELETE REVIEW =================

    @DeleteMapping("/admin/reviews/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable int id) {

        boolean removed =
                reviews.removeIf(
                        review -> review.id == id
                );

        if (removed) {

            return ResponseEntity.ok(
                    "Review removed successfully"
            );
        }

        return ResponseEntity.notFound()
                .build();
    }

    // ================= ADMIN PRODUCT SEARCH =================

    @GetMapping("/admin/laptops/search")
    public List<Laptop> adminSearch(
            @RequestParam String keyword) {

        List<Laptop> result =
                new ArrayList<>();

        for (Laptop laptop : laptops) {

            if (laptop.name
                    .toLowerCase()
                    .contains(
                            keyword.toLowerCase()
                    )
                    ||
                    laptop.brand
                            .toLowerCase()
                            .contains(
                                    keyword.toLowerCase()
                            )) {

                result.add(laptop);
            }
        }

        return result;
    }

    // ================= INVENTORY =================

    @GetMapping("/seller/inventory")
    public List<Laptop> inventory() {

        return laptops;
    }

    // ================= LOW STOCK PRODUCTS =================

    @GetMapping("/seller/inventory/low-stock")
    public List<Laptop> lowStockProducts() {

        List<Laptop> result =
                new ArrayList<>();

        for (Laptop laptop : laptops) {

            if (laptop.stock <= 5) {
                result.add(laptop);
            }
        }

        return result;
    }

    // ================= UPDATE STOCK =================

    @PutMapping("/seller/laptops/{id}/stock")
    public ResponseEntity<?> updateStock(
            @PathVariable int id,
            @RequestParam int stock) {

        if (stock < 0) {

            return ResponseEntity.badRequest()
                    .body(
                            "Stock cannot be negative"
                    );
        }

        Laptop laptop = findLaptop(id);

        if (laptop == null) {

            return ResponseEntity.notFound()
                    .build();
        }

        laptop.stock = stock;

        return ResponseEntity.ok(
                "Inventory updated successfully"
        );
    }

    // ================= PRODUCT COUNT =================

    @GetMapping("/products/count")
    public Map<String, Integer> productCount() {

        Map<String, Integer> response =
                new HashMap<>();

        response.put(
                "totalProducts",
                laptops.size()
        );

        return response;
    }

    // ================= ORDER STATUS =================

    @GetMapping("/orders/status/{status}")
    public List<Order> ordersByStatus(
            @PathVariable String status) {

        List<Order> result =
                new ArrayList<>();

        for (Order order : orders) {

            if (order.status.equalsIgnoreCase(
                    status)) {

                result.add(order);
            }
        }

        return result;
    }

    // ================= CATEGORY LIST =================

    @GetMapping("/categories")
    public List<String> categories() {

        Set<String> categorySet =
                new LinkedHashSet<>();

        for (Laptop laptop : laptops) {

            categorySet.add(
                    laptop.category
            );
        }

        return new ArrayList<>(
                categorySet
        );
    }

    // ================= BRAND LIST =================

    @GetMapping("/brands")
    public List<String> brands() {

        Set<String> brandSet =
                new LinkedHashSet<>();

        for (Laptop laptop : laptops) {

            brandSet.add(
                    laptop.brand
            );
        }

        return new ArrayList<>(
                brandSet
        );
    }

    // ================= PRICE FILTER =================

    @GetMapping("/laptops/filter")
    public List<Laptop> priceFilter(
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {

        List<Laptop> result =
                new ArrayList<>();

        for (Laptop laptop : laptops) {

            if (laptop.price >= minPrice
                    &&
                    laptop.price <= maxPrice) {

                result.add(laptop);
            }
        }

        return result;
    }

    // ================= SORT LOW TO HIGH =================

    @GetMapping("/laptops/sort/low-to-high")
    public List<Laptop> sortLowToHigh() {

        List<Laptop> result =
                new ArrayList<>(laptops);

        result.sort(
                Comparator.comparingDouble(
                        laptop -> laptop.price
                )
        );

        return result;
    }

    // ================= SORT HIGH TO LOW =================

    @GetMapping("/laptops/sort/high-to-low")
    public List<Laptop> sortHighToLow() {

        List<Laptop> result =
                new ArrayList<>(laptops);

        result.sort(
                Comparator.comparingDouble(
                        (Laptop laptop) ->
                                laptop.price
                ).reversed()
        );

        return result;
    }

    // ================= HEALTH CHECK =================

    @GetMapping("/health")
    public Map<String, String> healthCheck() {

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "application",
                "Karthika LaptopMart"
        );

        response.put(
                "status",
                "RUNNING"
        );

        response.put(
                "backend",
                "Java"
        );

        response.put(
                "api",
                "REST"
        );

        return response;
          }
}
