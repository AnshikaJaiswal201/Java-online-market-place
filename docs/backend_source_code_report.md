# ShopVerse — Backend Source Code (Project Report Documentation)

**Technology Stack:** Java 17, Spring Boot 3.2.5, Spring Security 6, Spring Data JPA, Hibernate ORM, JJWT (io.jsonwebtoken 0.11.5), HikariCP, MySQL 8.0  
**Architecture Pattern:** 3-Tier Enterprise Architecture (Controller -> Service -> Repository -> Relational Database), Stateless RESTful APIs with JWT Authentication & BCrypt Password Hashing.

---

## 1. Application Entry Point (`MarketplaceApplication.java`)
Configures Spring Boot auto-configuration, component scanning, and enables JPA entity auditing for timestamps.

```java
package com.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceApplication.class, args);
    }
}
```

---

## 2. Spring Security 6 & JWT Configuration (`SecurityConfig.java`)
Defines security filter chain, BCrypt password encoder bean, stateless session management, and role-based endpoint permissions.

```java
package com.marketplace.config;

import com.marketplace.security.JwtAuthenticationEntryPoint;
import com.marketplace.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public authentication endpoints
                .requestMatchers("/api/auth/**").permitAll()

                // Public product and category browsing
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

                // Role-based Seller endpoints
                .requestMatchers("/api/seller/**").hasAuthority("ROLE_SELLER")

                // Role-based Customer shopping cart
                .requestMatchers("/api/cart/**").hasAuthority("ROLE_CUSTOMER")

                // General authenticated actions (checkout, order history)
                .requestMatchers("/api/orders/**").authenticated()
                .requestMatchers("/api/customer/**").authenticated()

                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

---

## 3. JWT Token Provider (`JwtTokenProvider.java`)
Generates cryptographically signed HMAC-SHA256 tokens, embeds user claims, and validates incoming tokens.

```java
package com.marketplace.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("userId", userPrincipal.getUserId())
                .claim("fullName", userPrincipal.getFullName())
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
```

---

## 4. Authentication REST Controller (`AuthController.java`)
Exposes REST endpoints for user registration, login authentication, and profile retrieval.

```java
package com.marketplace.modules.auth.controller;

import com.marketplace.common.ApiResponse;
import com.marketplace.modules.auth.dto.AuthResponse;
import com.marketplace.modules.auth.dto.LoginRequest;
import com.marketplace.modules.auth.dto.RegisterRequest;
import com.marketplace.modules.auth.dto.UserProfileResponse;
import com.marketplace.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(ApiResponse.ok("User registered successfully", response), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(ApiResponse.error("Unauthorized"), HttpStatus.UNAUTHORIZED);
        }
        UserProfileResponse profile = authService.getCurrentUserProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok("User profile retrieved successfully", profile));
    }
}
```

---

## 5. Authentication & Registration Service (`AuthServiceImpl.java`)
Handles user creation, BCrypt password encryption, role assignment, and database persistence via JPA.

```java
package com.marketplace.modules.auth.service;

import com.marketplace.common.Constants;
import com.marketplace.exception.BadRequestException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.modules.auth.dto.AuthResponse;
import com.marketplace.modules.auth.dto.LoginRequest;
import com.marketplace.modules.auth.dto.RegisterRequest;
import com.marketplace.modules.auth.dto.UserProfileResponse;
import com.marketplace.modules.auth.entity.Role;
import com.marketplace.modules.auth.entity.User;
import com.marketplace.modules.auth.repository.RoleRepository;
import com.marketplace.modules.auth.repository.UserRepository;
import com.marketplace.security.CustomUserDetails;
import com.marketplace.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new AuthResponse(jwt, userPrincipal.getUserId(), userPrincipal.getUsername(),
                userPrincipal.getFullName(), roles);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Error: Email is already registered: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName() != null ? request.getFullName().trim() : "");
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddressLine1(request.getAddressLine1());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setPinCode(request.getPinCode());
        user.setIsActive(1);

        String roleStr = request.getRole() != null ? request.getRole().trim().toUpperCase() : "CUSTOMER";
        String normalizedRoleName = roleStr.startsWith("ROLE_") ? roleStr : "ROLE_" + roleStr;

        if (!normalizedRoleName.equals(Constants.ROLE_CUSTOMER) && !normalizedRoleName.equals(Constants.ROLE_SELLER)) {
            throw new BadRequestException("Invalid role specified. Allowed values: CUSTOMER, SELLER");
        }

        Role userRole = roleRepository.findByRoleName(normalizedRoleName)
                .orElseGet(() -> roleRepository.save(new Role(normalizedRoleName, "Role for " + normalizedRoleName)));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        // Authenticate new registered user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        List<String> roleNames = savedUser.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        return new AuthResponse(jwt, savedUser.getUserId(), savedUser.getEmail(), savedUser.getFullName(), roleNames);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(user.getUserId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddressLine1(user.getAddressLine1());
        response.setCity(user.getCity());
        response.setState(user.getState());
        response.setPinCode(user.getPinCode());
        response.setCreatedAt(user.getCreatedAt());
        response.setRoles(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()));

        return response;
    }
}
```

---

## 6. User Domain Entity (`User.java`)
JPA relational entity mapped to the `USERS` table in MySQL with auto-generated primary key and Many-to-Many role mapping.

```java
package com.marketplace.modules.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "EMAIL", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String password;

    @Column(name = "FULL_NAME", nullable = false, length = 100)
    private String fullName;

    @Column(name = "PHONE_NUMBER", length = 15)
    private String phoneNumber;

    @Column(name = "ADDRESS_LINE1", length = 200)
    private String addressLine1;

    @Column(name = "CITY", length = 50)
    private String city;

    @Column(name = "STATE", length = 50)
    private String state;

    @Column(name = "PIN_CODE", length = 10)
    private String pinCode;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Integer isActive = 1;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "USER_ROLES",
        joinColumns = @JoinColumn(name = "USER_ID"),
        inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Set<Role> roles = new HashSet<>();

    public User() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isEnabled() {
        return isActive != null && isActive == 1;
    }

    // Getters and Setters omitted for brevity
}
```

---

## 7. Product Catalog Controller (`ProductController.java`)
Provides public REST APIs for category listing, keyword search, and product retrieval.

```java
package com.marketplace.modules.product.controller;

import com.marketplace.common.ApiResponse;
import com.marketplace.modules.product.dto.CategoryDto;
import com.marketplace.modules.product.dto.ProductResponse;
import com.marketplace.modules.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getActiveProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        List<ProductResponse> products = productService.getActiveProducts(categoryId, keyword);
        return ResponseEntity.ok(ApiResponse.ok("Products retrieved successfully", products));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok("Product details retrieved", product));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getCategories() {
        List<CategoryDto> categories = productService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok("Categories retrieved successfully", categories));
    }
}
```

---

## 8. Order Checkout & Transaction Management (`OrderServiceImpl.java`)
Executes atomic stock verification, inventory deduction with `@Transactional` rollback protection, order code generation, and cart flushing.

```java
package com.marketplace.modules.order.service;

import com.marketplace.common.Constants;
import com.marketplace.exception.BadRequestException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.modules.auth.entity.User;
import com.marketplace.modules.auth.repository.UserRepository;
import com.marketplace.modules.inventory.service.InventoryService;
import com.marketplace.modules.order.dto.CheckoutRequest;
import com.marketplace.modules.order.dto.OrderResponse;
import com.marketplace.modules.order.entity.CartItem;
import com.marketplace.modules.order.entity.Order;
import com.marketplace.modules.order.entity.OrderItem;
import com.marketplace.modules.order.repository.CartItemRepository;
import com.marketplace.modules.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartItemRepository cartItemRepository,
                            UserRepository userRepository,
                            InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.inventoryService = inventoryService;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public OrderResponse checkout(Long customerId, CheckoutRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        List<CartItem> cartItems = cartItemRepository.findByUser_UserIdOrderByCreatedAtDesc(customerId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Your cart is empty. Add products before checking out.");
        }

        // Build Order
        Order order = new Order();
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomSuffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        order.setOrderNumber("ORD-" + dateStr + "-" + randomSuffix);
        order.setCustomer(customer);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "UPI");
        order.setPaymentStatus(Constants.PAYMENT_STATUS_PAID);
        order.setOrderStatus(Constants.ORDER_STATUS_PLACED);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Long productId = cartItem.getProduct().getProductId();
            int quantity = cartItem.getQuantity();

            // Atomic inventory deduction with rollback protection
            inventoryService.deductStock(productId, quantity);

            BigDecimal unitPrice = cartItem.getProduct().getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setSeller(cartItem.getProduct().getSeller());
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(unitPrice);
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // Clear shopping cart after successful checkout
        cartItemRepository.deleteAll(cartItems);

        return mapToOrderResponse(savedOrder);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setOrderNumber(order.getOrderNumber());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderStatus(order.getOrderStatus());
        response.setShippingAddress(order.getShippingAddress());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }
}
```

---

## 9. Database & JPA Configuration (`application-local.yml`)
Configures the MySQL 8.0 DataSource connection pool via HikariCP and Hibernate Dialect.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shopverse_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true
    username: root
    password: Nishi@123SQL
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      idle-timeout: 30000

  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
    show-sql: false

app:
  jwt:
    secret: 9a6e8b4c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a
    expiration-ms: 86400000 # 24 hours
```
