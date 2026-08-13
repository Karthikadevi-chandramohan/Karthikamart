package com.karthikamart;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class KarthikaMartApplication {

    private static final String JWT_SECRET =
            "KarthikaMartCapstoneSecretKeyForJWT123456789";

    private final JdbcTemplate db;
    private final PasswordEncoder encoder;

    public KarthikaMartApplication(JdbcTemplate db,
                                   PasswordEncoder encoder) {
        this.db = db;
        this.encoder = encoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(KarthikaMartApplication.class, args);
    }

    // ================= SECURITY =================

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable())
            .sessionManagement(s ->
                s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers(
                    "/api/auth/**",
                    "/api/products/**",
                    "/api/categories"
                ).permitAll()
                .requestMatchers("/api/cart/**", "/api/orders/**")
                .authenticated()
                .requestMatchers("/api/seller/**")
                .hasRole("SELLER")
                .requestMatchers("/api/admin/**")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated()
            )
            .addFilterBefore(
                new JwtFilter(),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    class JwtFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain chain)
                throws ServletException, IOException {

            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                try {
                    String token = header.substring(7);

                    var claims = Jwts.parser()
                            .verifyWith(
                                Keys.hmacShaKeyFor(
                                    JWT_SECRET.getBytes(
                                        StandardCharsets.UTF_8
                                    )
                                )
                            )
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

                    String email = claims.getSubject();
                    String role = claims.get("role", String.class);

                    var authentication =
                            new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(
                                    new org.springframework
                                        .security.core.authority
                                        .SimpleGrantedAuthority(
                                            "ROLE_" + role
                                        )
                                )
                            );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                } catch (Exception ignored) {
                }
            }

            chain.doFilter(request, response);
        }
    }

    // ================= DATABASE =================

    @Bean
    org.springframework.boot.CommandLineRunner initializeDatabase() {
        return args -> {

            db.execute("""
                CREATE TABLE IF NOT EXISTS users(
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(150) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    role VARCHAR(20) NOT NULL
                )
            """);

            db.execute("""
                CREATE TABLE IF NOT EXISTS categories(
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(100) UNIQUE NOT NULL
                )
            """);

            db.execute("""
                CREATE TABLE IF NOT EXISTS products(
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(200) NOT NULL,
                    description TEXT,
                    price NUMERIC(12,2) NOT NULL,
                    stock INT NOT NULL,
                    image_url TEXT,
                    category_id BIGINT
                        REFERENCES categories(id),
                    seller_id BIGINT
                        REFERENCES users(id)
                )
            """);

            db.execute("""
                CREATE TABLE IF NOT EXISTS carts(
                    id BIGSERIAL PRIMARY KEY,
                    user_id BIGINT UNIQUE
                        REFERENCES users(id)
                )
            """);

            db.execute("""
                CREATE TABLE IF NOT EXISTS cart_items(
                    id BIGSERIAL PRIMARY KEY,
                    cart_id BIGINT
                        REFERENCES carts(id) ON DELETE CASCADE,
                    product_id BIGINT
                        REFERENCES products(id),
                    quantity INT NOT NULL,
                    UNIQUE(cart_id, product_id)
                )
            """);

            db.execute("""
                CREATE TABLE IF NOT EXISTS orders(
                    id BIGSERIAL PRIMARY KEY,
                    buyer_id BIGINT
                        REFERENCES users(id),
                    total_amount NUMERIC(12,2) NOT NULL,
                    status VARCHAR(30) NOT NULL,
                    created_at TIMESTAMP NOT NULL
                )
            """);

            db.execute("""
                CREATE TABLE IF NOT EXISTS order_items(
                    id BIGSERIAL PRIMARY KEY,
                    order_id BIGINT
                        REFERENCES orders(id) ON DELETE CASCADE,
                    product_id BIGINT
                        REFERENCES products(id),
                    quantity INT NOT NULL,
                    unit_price NUMERIC(12,2) NOT NULL
                )
            """);

            seedData();
        };
    }

    private void seedData() {

        Integer sellerExists = db.queryForObject(
            "SELECT COUNT(*) FROM users WHERE email=?",
            Integer.class,
            "seller@karthikamart.com"
        );

        if (sellerExists != null && sellerExists == 0) {

            db.update("""
                INSERT INTO users
                (name,email,password,role)
                VALUES(?,?,?,?)
            """,
                "Demo Seller",
                "seller@karthikamart.com",
                encoder.encode("Seller@123"),
                "SELLER"
            );
        }

        addCategory("Electronics");
        addCategory("Books");

        Integer productCount =
                db.queryForObject(
                    "SELECT COUNT(*) FROM products",
                    Integer.class
                );

        if (productCount != null && productCount == 0) {

            Long sellerId = db.queryForObject(
                "SELECT id FROM users WHERE email=?",
                Long.class,
                "seller@karthikamart.com"
            );

            Long electronicsId = db.queryForObject(
                "SELECT id FROM categories WHERE name=?",
                Long.class,
                "Electronics"
            );

            Long booksId = db.queryForObject(
                "SELECT id FROM categories WHERE name=?",
                Long.class,
                "Books"
            );

            db.update("""
                INSERT INTO products
                (name,description,price,stock,
                 image_url,category_id,seller_id)
                VALUES(?,?,?,?,?,?,?)
            """,
                "Wireless Headphones",
                "Bluetooth wireless headphones",
                new BigDecimal("1499.00"),
                20,
                "",
                electronicsId,
                sellerId
            );

            db.update("""
                INSERT INTO products
                (name,description,price,stock,
                 image_url,category_id,seller_id)
                VALUES(?,?,?,?,?,?,?)
            """,
                "Java Programming Book",
                "Beginner Java programming book",
                new BigDecimal("599.00"),
                15,
                "",
                booksId,
                sellerId
            );
        }
    }

    private void addCategory(String name) {

        Integer count = db.queryForObject(
            "SELECT COUNT(*) FROM categories WHERE name=?",
            Integer.class,
            name
        );

        if (count != null && count == 0) {
            db.update(
                "INSERT INTO categories(name) VALUES(?)",
                name
            );
        }
    }

    // ================= AUTH =================

    public record RegisterRequest(
            String name,
            String email,
            String password,
            String role) {}

    public record LoginRequest(
            String email,
            String password) {}

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        if (request.name() == null ||
            request.name().isBlank() ||
            request.email() == null ||
            request.email().isBlank() ||
            request.password() == null ||
            request.password().length() < 6) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "error",
                        "Invalid registration data"
                    ));
        }

        String email =
                request.email().trim().toLowerCase();

        Integer exists = db.queryForObject(
            "SELECT COUNT(*) FROM users WHERE email=?",
            Integer.class,
            email
        );

        if (exists != null && exists > 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "error",
                        "Email already registered"
                    ));
        }

        String role =
                request.role() == null
                ? "BUYER"
                : request.role().toUpperCase();

        if (!role.equals("BUYER") &&
            !role.equals("SELLER")) {

            role = "BUYER";
        }

        db.update("""
            INSERT INTO users
            (name,email,password,role)
            VALUES(?,?,?,?)
        """,
            request.name().trim(),
            email,
            encoder.encode(request.password()),
            role
        );

        Long userId = db.queryForObject(
            "SELECT id FROM users WHERE email=?",
            Long.class,
            email
        );

        db.update(
            "INSERT INTO carts(user_id) VALUES(?)",
            userId
        );

        return ResponseEntity.ok(
            Map.of(
                "message", "Registration successful",
                "userId", userId,
                "role", role
            )
        );
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        List<Map<String, Object>> users =
                db.queryForList(
                    "SELECT * FROM users WHERE email=?",
                    request.email()
                        .trim()
                        .toLowerCase()
                );

        if (users.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                        "error",
                        "Invalid email or password"
                    ));
        }

        Map<String, Object> user = users.get(0);

        if (!encoder.matches(
                request.password(),
                (String) user.get("password"))) {

            return ResponseEntity.status(401)
                    .body(Map.of(
                        "error",
                        "Invalid email or password"
                    ));
        }

        String role =
                (String) user.get("role");

        String token = Jwts.builder()
                .subject((String) user.get("email"))
                .claim("role", role)
                .claim(
                    "userId",
                    ((Number) user.get("id"))
                        .longValue()
                )
                .issuedAt(new Date())
                .expiration(
                    new Date(
                        System.currentTimeMillis()
                        + 86400000
                    )
                )
                .signWith(
                    Keys.hmacShaKeyFor(
                        JWT_SECRET.getBytes(
                            StandardCharsets.UTF_8
                        )
                    )
                )
                .compact();

        return ResponseEntity.ok(
            Map.of(
                "message", "Login successful",
                "token", token,
                "role", role,
                "userId", user.get("id")
            )
        );
    }

    // ================= PRODUCTS =================

    @GetMapping("/products")
    public List<Map<String, Object>> getProducts(
            @RequestParam(required = false)
            String keyword,
            @RequestParam(required = false)
            Long categoryId) {

        StringBuilder sql =
                new StringBuilder("""
                    SELECT p.*,
                           c.name AS category_name
                    FROM products p
                    LEFT JOIN categories c
                    ON c.id=p.category_id
                    WHERE 1=1
                """);

        List<Object> params =
                new ArrayList<>();

        if (keyword != null &&
            !keyword.isBlank()) {

            sql.append(
                " AND LOWER(p.name) LIKE LOWER(?)"
            );

            params.add("%" + keyword + "%");
        }

        if (categoryId != null) {

            sql.append(
                " AND p.category_id=?"
            );

            params.add(categoryId);
        }

        sql.append(" ORDER BY p.id DESC");

        return db.queryForList(
            sql.toString(),
            params.toArray()
        );
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProduct(
            @PathVariable Long id) {

        List<Map<String, Object>> result =
                db.queryForList("""
                    SELECT p.*,
                           c.name AS category_name
                    FROM products p
                    LEFT JOIN categories c
                    ON c.id=p.category_id
                    WHERE p.id=?
                """, id);

        if (result.isEmpty()) {
            return ResponseEntity.notFound()
                    .build();
        }

        return ResponseEntity.ok(
                result.get(0)
        );
    }

    @GetMapping("/categories")
    public List<Map<String, Object>>
    getCategories() {

        return db.queryForList(
            "SELECT * FROM categories ORDER BY name"
        );
    }

    // ================= CART =================

    public record CartRequest(
            Long productId,
            Integer quantity) {}

    private Long currentUserId() {

        String email =
            SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return db.queryForObject(
            "SELECT id FROM users WHERE email=?",
            Long.class,
            email
        );
    }

    @GetMapping("/cart")
    public List<Map<String, Object>>
    getCart() {

        Long userId = currentUserId();

        return db.queryForList("""
            SELECT
                ci.id AS cart_item_id,
                p.id AS product_id,
                p.name,
                p.price,
                p.stock,
                ci.quantity,
                (p.price * ci.quantity)
                AS subtotal
            FROM cart_items ci
            JOIN carts c
            ON c.id=ci.cart_id
            JOIN products p
            ON p.id=ci.product_id
            WHERE c.user_id=?
            ORDER BY ci.id
        """, userId);
    }

    @PostMapping("/cart/items")
    public ResponseEntity<?> addToCart(
            @RequestBody CartRequest request) {

        if (request.productId() == null ||
            request.quantity() == null ||
            request.quantity() <= 0) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "error",
                        "Invalid quantity"
                    ));
        }

        Long userId =
                currentUserId();

        Integer stock =
                db.queryForObject(
                    "SELECT stock FROM products WHERE id=?",
                    Integer.class,
                    request.productId()
                );

        if (stock == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "error",
                        "Product not found"
                    ));
        }

        if (request.quantity() > stock) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "error",
                        "Not enough stock"
                    ));
        }

        Long cartId =
                db.queryForObject(
                    "SELECT id FROM carts WHERE user_id=?",
                    Long.class,
                    userId
                );

        Integer existing =
                db.queryForObject("""
                    SELECT COUNT(*)
                    FROM cart_items
                    WHERE cart_id=?
                    AND product_id=?
                """,
                    Integer.class,
                    cartId,
                    request.productId()
                );

        if
