# ShopVerse &mdash; Online Market Place (E-Commerce Web Application)

An enterprise multi-vendor e-commerce web application built with **Java 17 / Spring Boot 3.x**, **Angular 14**, and **MySQL 8.0 / Oracle Database (normalized 3NF schema)**.

---

## 🚀 Tech Stack

- **Backend**: Java 17, Spring Boot 3.2.5, Spring Data JPA, Hibernate, Spring Security 6 (JWT + BCrypt), Jakarta Validation, HikariCP.
- **Frontend**: Angular 14, TypeScript, RxJS 7.x, Reactive Forms, FontAwesome 6, Custom INR Currency Pipe.
- **Database**: MySQL 8.0 / Oracle Database 21c (3NF Normalized Schema with relational tables for Users, Roles, Categories, Products, Inventory, Orders).
- **Architecture**: 3-Tier Enterprise Architecture (Client SPA, REST API Service, Relational DB).

---

## 📦 Project Structure

```text
Minor Project/
├── database/
│   ├── mysql_schema.sql           # Complete MySQL 8.0 DDL & Seed script (24 Indian Products, 4 Categories, Roles)
│   ├── schema.sql                 # Oracle 21c DDL script
│   └── data.sql                   # Oracle seed data
│
├── docs/
│   ├── system_context_diagram_dfd0.svg   # Vector DFD Level 0 System Context Diagram
│   └── system_context_diagram_dfd0.png   # High-Res Image for Project Reports / Word Document
│
├── backend/                       # Spring Boot 3.x Application
│   ├── pom.xml                    # Maven build file with Java 17, MySQL Connector, JJWT, Spring Boot starters
│   └── src/main/
│       ├── java/com/marketplace/
│       │   ├── MarketplaceApplication.java
│       │   ├── config/            # SecurityConfig, WebMvcConfig (CORS), JpaAuditing
│       │   ├── security/          # JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetails
│       │   ├── common/            # ApiResponse<T>, Constants
│       │   ├── exception/         # GlobalExceptionHandler, InsufficientStockException
│       │   └── modules/
│       │       ├── auth/          # Authentication & RBAC (CUSTOMER, SELLER)
│       │       ├── product/       # Product Catalog CRUD & Soft Delete
│       │       ├── inventory/     # Real-time stock & Atomic @Transactional deduction
│       │       ├── order/         # Cart, Mock Checkout, Order Tracking
│       │       └── dashboard/     # Seller revenue (INR), orders, low-stock metrics
│       └── resources/
│           ├── application.yml          # Production/Oracle configuration
│           └── application-local.yml    # MySQL 8.0 local DataSource & JPA configuration
│
└── frontend/                      # Angular 14 SPA
    ├── package.json
    ├── angular.json
    └── src/app/
        ├── core/                  # AuthService, ProductService, CartService, OrderService, SellerService, Guards, Interceptors
        ├── shared/                # Navbar, Footer, InrCurrencyPipe, Models
        └── features/
            ├── auth/              # Login & Register with Role Selector (Customer / Seller)
            ├── customer/          # Product Catalog, Shopping Cart, Checkout, Order History
            └── seller/            # Seller Dashboard, Product CRUD, Stock Restock, Incoming Orders
```

---

## 🛠️ Step-by-Step Setup Guide

### 1. Database Setup (MySQL 8.0)
1. Ensure your local MySQL 8.0 service is running on port 3306.
2. In MySQL Workbench or Command Line, execute:
   ```sql
   SOURCE database/mysql_schema.sql;
   ```
   This creates database `shopverse_db` with all 9 tables and seeds 24 products across 4 categories (Electronics, Traditional Wear, Spices, Handicrafts).

---

### 2. Backend Setup (Spring Boot 3.x)
1. Navigate to the `backend/` directory:
   ```bash
   cd backend
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```
2. Backend starts on **`http://localhost:8080`**.

---

### 3. Frontend Setup (Angular 14)
1. Navigate to the `frontend/` directory:
   ```bash
   cd frontend
   npm install
   npm start
   ```
2. Frontend runs on **`http://localhost:4200`**.

---

## 👥 Default Demo Credentials

- **Seller / Merchant**: `seller.sharma@marketplace.in` / `Password@123`
- **Customer**: `aarav.patel@gmail.com` / `Password@123`
- **New Users**: Can register directly via the UI at `/auth/register` (data persists directly to MySQL).
   ```
4. Open your browser and navigate to: **`http://localhost:4200`**

---

## 👤 Pre-Seeded Test Accounts

All seeded accounts have the default password: **`Password@123`**

| Role | Name | Email | Details |
| :--- | :--- | :--- | :--- |
| **SELLER** | Rajesh Sharma | `seller.sharma@marketplace.in` | Electronics Hub, Bengaluru (Audio, Gadgets) |
| **SELLER** | Meera Singhania | `seller.meera@marketplace.in` | Handlooms & Decor, Jaipur (Silk Sarees, Blue Pottery) |
| **CUSTOMER** | Aarav Patel | `aarav.patel@gmail.com` | Ahmedabad, Gujarat |
| **CUSTOMER** | Priya Nair | `priya.nair@yahoo.co.in` | Kochi, Kerala (Has active items in cart) |
| **CUSTOMER** | Rohan Gupta | `rohan.gupta@outlook.com` | Lucknow, Uttar Pradesh |

> **Tip:** You can also register brand-new Customer or Seller accounts directly from the UI register page.

---

## 🌟 Key Application Features

1. **Role-Based Access Control (RBAC)**:
   - Dedicated navigation and access guards for `CUSTOMER` vs `SELLER`.
   - Sellers can only access merchant tools, while customers can browse, cart, and checkout.
2. **Product Catalog & Soft-Delete**:
   - Products include Name, Description, Category, Price in INR (`₹`), SKU, and Image URL.
   - Sellers can toggle active/inactive status without hard-deleting records or corrupting foreign keys.
3. **Atomic Stock Deduction**:
   - Executed under `@Transactional(isolation = Isolation.READ_COMMITTED)`.
   - Prevents race conditions and negative inventory during simultaneous checkouts.
   - Automatically restocks inventory if an order is cancelled.
4. **Seller Dashboard**:
   - Live revenue calculation in INR (`₹`).
   - Order counts and active product counters.
   - Automated low-stock alerts when `stock_quantity <= low_stock_threshold`.
