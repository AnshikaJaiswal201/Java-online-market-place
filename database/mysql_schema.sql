-- ============================================================================
-- SHOPVERSE E-COMMERCE - MYSQL 8.0 WORKBENCH SCHEMA & SEED DATA SCRIPT
-- ============================================================================

CREATE DATABASE IF NOT EXISTS shopverse_db;
USE shopverse_db;

-- 1. Roles
CREATE TABLE IF NOT EXISTS roles (
    role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 2. Users
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    address_line1 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    pin_code VARCHAR(10),
    is_active TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. User Roles (Junction Table)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

-- 4. Categories
CREATE TABLE IF NOT EXISTS categories (
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    is_active TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Products
CREATE TABLE IF NOT EXISTS products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(12, 2) NOT NULL,
    sku VARCHAR(100) NOT NULL UNIQUE,
    image_url VARCHAR(1000),
    is_active TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES users(user_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- 6. Inventory
CREATE TABLE IF NOT EXISTS inventory (
    inventory_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    stock_quantity INT NOT NULL DEFAULT 0,
    low_stock_threshold INT NOT NULL DEFAULT 5,
    last_restocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

-- 7. Cart Items
CREATE TABLE IF NOT EXISTS cart_items (
    cart_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE KEY uq_user_product (user_id, product_id)
);

-- 8. Orders
CREATE TABLE IF NOT EXISTS orders (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(64) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    order_status VARCHAR(50) NOT NULL DEFAULT 'PLACED',
    shipping_address TEXT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(user_id)
);

-- 9. Order Items
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (seller_id) REFERENCES users(user_id)
);

-- ============================================================================
-- SEED DATA (24 PRODUCTS, USERS, INVENTORY, ORDERS)
-- ============================================================================

-- Roles
INSERT IGNORE INTO roles (role_id, role_name, description) VALUES (1, 'ROLE_CUSTOMER', 'Customer role for shopping and placing orders');
INSERT IGNORE INTO roles (role_id, role_name, description) VALUES (2, 'ROLE_SELLER', 'Merchant / Seller role for catalog and stock management');

-- Users (Default password: Password@123)
INSERT IGNORE INTO users (user_id, email, password_hash, full_name, phone_number, address_line1, city, state, pin_code, is_active)
VALUES (1, 'seller.sharma@marketplace.in', '$2a$10$7vN5AUpuT8Zt9v/Fm7c1IeK2hWb6u3N4q1P5R7T9X0V2Y3Z4A5B6C', 'Rajesh Sharma', '+919876543210', 'Shop 42, SP Road Electronics Complex', 'Bengaluru', 'Karnataka', '560002', 1);

INSERT IGNORE INTO users (user_id, email, password_hash, full_name, phone_number, address_line1, city, state, pin_code, is_active)
VALUES (2, 'seller.meera@marketplace.in', '$2a$10$7vN5AUpuT8Zt9v/Fm7c1IeK2hWb6u3N4q1P5R7T9X0V2Y3Z4A5B6C', 'Meera Singhania', '+919811223344', 'Plot 18, Johari Bazaar Weavers Lane', 'Jaipur', 'Rajasthan', '302003', 1);

INSERT IGNORE INTO users (user_id, email, password_hash, full_name, phone_number, address_line1, city, state, pin_code, is_active)
VALUES (3, 'aarav.patel@gmail.com', '$2a$10$7vN5AUpuT8Zt9v/Fm7c1IeK2hWb6u3N4q1P5R7T9X0V2Y3Z4A5B6C', 'Aarav Patel', '+919988776655', 'Flat 402, Nilgiri Heights, Vastrapur', 'Ahmedabad', 'Gujarat', '380015', 1);

INSERT IGNORE INTO users (user_id, email, password_hash, full_name, phone_number, address_line1, city, state, pin_code, is_active)
VALUES (4, 'priya.nair@yahoo.co.in', '$2a$10$7vN5AUpuT8Zt9v/Fm7c1IeK2hWb6u3N4q1P5R7T9X0V2Y3Z4A5B6C', 'Priya Nair', '+919447112233', '12/450, Rose Gardens, Panampilly Nagar', 'Kochi', 'Kerala', '682036', 1);

-- User Roles
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (1, 2);
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (3, 1);
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (4, 1);

-- Categories
INSERT IGNORE INTO categories (category_id, category_name, description) VALUES (1, 'Electronics & Gadgets', 'Audio accessories, smart wearables, and mobile peripherals');
INSERT IGNORE INTO categories (category_id, category_name, description) VALUES (2, 'Traditional Indian Wear', 'Authentic handloom sarees, kurtas, and festive ethnic clothing');
INSERT IGNORE INTO categories (category_id, category_name, description) VALUES (3, 'Spices & Gourmet', 'Farm-fresh organic spices and wellness essentials from South India');
INSERT IGNORE INTO categories (category_id, category_name, description) VALUES (4, 'Handicrafts & Home Decor', 'Handcrafted pottery, brass lamps, and artisanal artifacts');

-- Products (24 Items)
INSERT IGNORE INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url) VALUES
(1, 1, 1, 'boAt Rockerz 550 Wireless Bluetooth Headphones', 'Over-ear wireless headphones with 50mm dynamic drivers, up to 20 hours playback.', 1999.00, 'BOAT-ROC-550-BLK', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600'),
(2, 1, 1, 'Noise ColorFit Pulse Grand Smartwatch', '1.69 inch HD display smartwatch with 60 sports modes, SpO2 & heart rate monitor.', 1499.00, 'NOIS-CFP-GRD-BLU', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600'),
(3, 1, 1, 'Portronics Konnect L 1.2M Fast Charging Cable', 'Tangle-free braided Type-C to Lightning 20W PD cable with reinforced connectors.', 299.00, 'PORT-CB-LGT-20W', 'https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=600'),
(4, 1, 1, 'OnePlus Bullets Z2 Wireless Bluetooth Earphones', 'Magnetic neckband with 12.4mm deep bass drivers, 10-min charge for 20 hrs playback.', 1699.00, 'OP-BUL-Z2-BLK', 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600'),
(5, 1, 1, 'Mi 10000mAh Power Bank 3i (18W Fast Charging)', 'Dual output USB-A ports, dual input Type-C and Micro-USB, 12 layers protection.', 1299.00, 'MI-PB-10K-BLK', 'https://images.unsplash.com/photo-1609592424364-e4612d1b7a24?w=600'),
(6, 1, 1, 'Zebronics Zeb-Warrior 2.0 RGB Desktop Speakers', 'Compact USB powered desktop speakers with vibrant RGB breathing lights.', 849.00, 'ZEB-WAR-SPK-RGB', 'https://images.unsplash.com/photo-1545454675-3531b543be5d?w=600'),
(7, 2, 2, 'Pure Kanjivaram Silk Saree (Crimson Red & Gold Zari)', 'Handwoven pure mulberry silk saree featuring traditional temple borders and gold zari.', 6499.00, 'KANJ-SILK-RED-01', 'https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600'),
(8, 2, 2, 'Lucknowi Chikankari Men Cotton Kurta (Pastel Mint)', 'Breathable premium cambric cotton straight kurta with hand-embroidered floral motifs.', 2199.00, 'LUCK-KRT-MNT-42', 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600'),
(9, 2, 2, 'Banarasi Brocade Semi-Stitched Lehenga Choli', 'Exquisite woven zari work semi-stitched lehenga with floral border and organza dupatta.', 5899.00, 'BANA-LEH-BLU-02', 'https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?w=600'),
(10, 2, 2, 'Jaipuri Bandhani Pure Mulmul Cotton Anarkali Kurti', '100% pure mulmul cotton flared Anarkali kurta featuring authentic tie-and-dye craft.', 1850.00, 'JAIP-BAND-KUR-38', 'https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?w=600'),
(11, 2, 2, 'Royal Jodhpuri Bandhgala Silk Blend Blazer (Jet Black)', 'Tailored slim-fit mandarin collar royal Jodhpuri jacket with engraved metallic buttons.', 4499.00, 'JODH-BND-BLK-40', 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=600'),
(12, 2, 2, 'Chanderi Handloom Festive Saree with Golden Zari', 'Lightweight sheer Chanderi cotton-silk drape crafted by Madhya Pradesh weavers.', 3299.00, 'CHAN-RES-GLD-05', 'https://images.unsplash.com/photo-1610030469668-932ec596e73c?w=600'),
(13, 1, 3, 'Wayanad Organic Whole Black Pepper (Malabar Gold)', 'Sun-dried bold black peppercorns grown in Wayanad, Kerala. 500g vacuum pouch.', 480.00, 'WAYA-PEP-500G-01', 'https://images.unsplash.com/photo-1599940824399-b87987ceb72a?w=600'),
(14, 1, 3, 'Kashmiri Mongra Saffron (Pure Kesar, Grade 1 A++)', 'Pampore origin 100% pure all-red stigma tips. 1g airtight blister.', 950.00, 'KASH-SAF-1G-01', 'https://images.unsplash.com/photo-1615485500704-8e990f9900f7?w=600'),
(15, 1, 3, 'Guntur Teja Dry Red Chillies (Stemless, Extra Spicy)', 'High pungency, bright fiery red color from Guntur, Andhra Pradesh. 500g pack.', 290.00, 'GUNT-CHL-500G-02', 'https://images.unsplash.com/photo-1588252303782-cb80119abd6d?w=600'),
(16, 1, 3, 'Coorg Green Cardamom (8mm Jumbo Alleppey Extra Bold)', 'Extra bold aromatic cardamom pods from Coorg, Karnataka. 250g pouch.', 720.00, 'COOR-ELA-250G-03', 'https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=600'),
(17, 1, 3, 'Salem Turmeric Finger (High Curcumin 5% Unpolished)', 'Naturally golden dried whole turmeric fingers from Salem, Tamil Nadu. 500g pack.', 240.00, 'SALM-HLD-500G-04', 'https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600'),
(18, 1, 3, 'Darjeeling First Flush Whole Leaf Black Tea', 'Single estate spring harvest tea plucked from slopes of Darjeeling. 200g tin caddy.', 850.00, 'DARJ-TEA-200G-05', 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=600'),
(19, 2, 4, 'Jaipur Blue Pottery Handcrafted Ceramic Flower Vase', 'Traditional quartz and glass fired blue pottery from Jaipur artisans. Height 10 inches.', 1150.00, 'JAIP-POT-VAS-10', 'https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=600'),
(20, 2, 4, 'Moradabad Brass Peacock Diya Oil Lamp (Set of 2)', 'Handcrafted solid brass diyas with peacock crests. Antique finish.', 1450.00, 'MORA-BRS-DYA-02', 'https://images.unsplash.com/photo-1605371924599-2d0365da1ae0?w=600'),
(21, 2, 4, 'Saharanpur Hand-Carved Sheesham Wood Jewelry Box', 'Solid rosewood keepsake box with floral jaali filigree lid. 8x5x3 inches.', 899.00, 'SAHR-WD-JWL-BOX', 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=600'),
(22, 2, 4, 'Bastar Iron Craft Tribal Musician Figurine (Dokra Art)', 'Lost-wax cast bell metal sculpture created by tribal artisans of Chhattisgarh.', 1299.00, 'BAST-IRN-TRB-FIG', 'https://images.unsplash.com/photo-1582562124811-c09040d0a901?w=600'),
(23, 2, 4, 'Kashmir Hand-Embroidered Crewel Wool Cushion Covers (Pair)', 'Wool chain stitch Aari needlework on unbleached heavy cotton duck fabric. 16x16 inches.', 1199.00, 'KASH-CRW-CSH-16', 'https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?w=600'),
(24, 2, 4, 'Terracotta Hand-Painted Warli Art Table Lamp', 'Earthy clay pot lamp handcrafted with tribal Warli motifs. Height 12 inches.', 980.00, 'WARL-TER-LMP-01', 'https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=600');

-- Inventory
INSERT IGNORE INTO inventory (product_id, stock_quantity, low_stock_threshold) VALUES
(1, 40, 5), (2, 25, 5), (3, 3, 5), (4, 35, 5), (5, 20, 4), (6, 15, 3),
(7, 12, 3), (8, 18, 4), (9, 10, 2), (10, 22, 4), (11, 14, 3), (12, 16, 3),
(13, 50, 8), (14, 30, 5), (15, 40, 6), (16, 28, 5), (17, 45, 7), (18, 25, 4),
(19, 2, 4), (20, 18, 3), (21, 20, 4), (22, 8, 2), (23, 15, 3), (24, 12, 3);
