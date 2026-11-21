-- ============================================================================
-- ONLINE MARKETPLACE - SAMPLE INDIAN-CONTEXT DATA SCRIPT
-- Database: Oracle Database 21c XE
-- Note: Default password for all users is "Password@123"
-- BCrypt Hash: $2a$10$e8wF5qG9wV6B4Fp0tI8h5O7yE6H1M5n6T4X3V8o2Q1R9s7A6d5C8e
-- ============================================================================

-- 1. Insert Roles
INSERT INTO roles (role_id, role_name, description) VALUES (1, 'ROLE_CUSTOMER', 'Standard customer role for placing orders');
INSERT INTO roles (role_id, role_name, description) VALUES (2, 'ROLE_SELLER', 'Merchant/Seller role for catalog and inventory management');

-- 2. Insert Users (Sellers & Customers)
-- Default Password for all test users: Password@123
-- Hash: $2a$10$2l1Z2b3o9Fk2B/X5X7m8uuq7ZqR6uO1WzM3iG9.i9vGfXz3a1B2c3 (Standard BCrypt format)

-- Sellers
INSERT INTO users (user_id, email, password_hash, full_name, phone_number, address_line1, city, state, pin_code, is_active)
VALUES (1001, 'seller.sharma@marketplace.in', '$2a$10$7vN5AUpuT8Zt9v/Fm7c1IeK2hWb6u3N4q1P5R7T9X0V2Y3Z4A5B6C', 'Rajesh Sharma', '+919876543210', 'Shop 42, SP Road Electronics Complex', 'Bengaluru', 'Karnataka', '560002', 1);

INSERT INTO users (user_id, email, password_hash, full_name, phone_number, address_line1, city, state, pin_code, is_active)
VALUES (1002, 'seller.meera@marketplace.in', '$2a$10$7vN5AUpuT8Zt9v/Fm7c1IeK2hWb6u3N4q1P5R7T9X0V2Y3Z4A5B6C', 'Meera Singhania', '+919811223344', 'Plot 18, Johari Bazaar Weavers Lane', 'Jaipur', 'Rajasthan', '302003', 1);

-- Customers
INSERT INTO users (user_id, email, password_hash, full_name, phone_number, address_line1, city, state, pin_code, is_active)
VALUES (1003, 'aarav.patel@gmail.com', '$2a$10$7vN5AUpuT8Zt9v/Fm7c1IeK2hWb6u3N4q1P5R7T9X0V2Y3Z4A5B6C', 'Aarav Patel', '+919988776655', 'Flat 402, Nilgiri Heights, Vastrapur', 'Ahmedabad', 'Gujarat', '380015', 1);

INSERT INTO users (user_id, email, password_hash, full_name, phone_number, address_line1, city, state, pin_code, is_active)
VALUES (1004, 'priya.nair@yahoo.co.in', '$2a$10$7vN5AUpuT8Zt9v/Fm7c1IeK2hWb6u3N4q1P5R7T9X0V2Y3Z4A5B6C', 'Priya Nair', '+919447112233', '12/450, Rose Gardens, Panampilly Nagar', 'Kochi', 'Kerala', '682036', 1);

INSERT INTO users (user_id, email, password_hash, full_name, phone_number, address_line1, city, state, pin_code, is_active)
VALUES (1005, 'rohan.gupta@outlook.com', '$2a$10$7vN5AUpuT8Zt9v/Fm7c1IeK2hWb6u3N4q1P5R7T9X0V2Y3Z4A5B6C', 'Rohan Gupta', '+919721004455', 'B-28, Hazratganj Main Road', 'Lucknow', 'Uttar Pradesh', '226001', 1);

-- 3. Assign Roles
INSERT INTO user_roles (user_id, role_id) VALUES (1001, 2); -- Rajesh: SELLER
INSERT INTO user_roles (user_id, role_id) VALUES (1002, 2); -- Meera: SELLER
INSERT INTO user_roles (user_id, role_id) VALUES (1003, 1); -- Aarav: CUSTOMER
INSERT INTO user_roles (user_id, role_id) VALUES (1004, 1); -- Priya: CUSTOMER
INSERT INTO user_roles (user_id, role_id) VALUES (1005, 1); -- Rohan: CUSTOMER

-- 4. Insert Categories
INSERT INTO categories (category_id, category_name, description, is_active) 
VALUES (11, 'Electronics & Gadgets', 'Audio accessories, smart wearables, and mobile peripherals', 1);

INSERT INTO categories (category_id, category_name, description, is_active) 
VALUES (12, 'Traditional Indian Wear', 'Authentic handloom sarees, kurtas, and festive ethnic clothing', 1);

INSERT INTO categories (category_id, category_name, description, is_active) 
VALUES (13, 'Spices & Gourmet', 'Farm-fresh organic spices and wellness essentials from South India', 1);

INSERT INTO categories (category_id, category_name, description, is_active) 
VALUES (14, 'Handicrafts & Home Decor', 'Handcrafted pottery, brass lamps, and artisanal artifacts', 1);

-- 5. Insert Products (INR Pricing) - 6 items per category
-- CATEGORY 11: Electronics & Gadgets (Seller: Rajesh Sharma, user_id=1001)
INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5001, 1001, 11, 'boAt Rockerz 550 Wireless Bluetooth Headphones', 'Over-ear wireless headphones with 50mm dynamic drivers, up to 20 hours playback, noise isolation, and extra bass.', 1999.00, 'BOAT-ROC-550-BLK', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5002, 1001, 11, 'Noise ColorFit Pulse Grand Smartwatch', '1.69 inch HD display smartwatch with 60 sports modes, SpO2 & 24x7 heart rate monitoring, fast charging, and 150+ watch faces.', 1499.00, 'NOIS-CFP-GRD-BLU', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5003, 1001, 11, 'Portronics Konnect L 1.2M Fast Charging Cable', 'Tangle-free braided Type-C to Lightning 20W PD cable with reinforced aluminum connectors for heavy daily commuting.', 299.00, 'PORT-CB-LGT-20W', 'https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5007, 1001, 11, 'OnePlus Bullets Z2 Wireless Bluetooth Earphones', 'Acoustically tuned magnetic neckband with 12.4mm deep bass drivers, 10-minute fast charging for 20 hours playback, and IP55 resistance.', 1699.00, 'OP-BUL-Z2-BLK', 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5008, 1001, 11, 'Mi 10000mAh Power Bank 3i (18W Fast Charging)', 'Dual output USB-A ports, dual input Type-C and Micro-USB, 12 layers circuit protection, and smart power management.', 1299.00, 'MI-PB-10K-BLK', 'https://images.unsplash.com/photo-1609592424364-e4612d1b7a24?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5009, 1001, 11, 'Zebronics Zeb-Warrior 2.0 RGB Desktop Speakers', 'Compact USB powered desktop speakers with vibrant RGB breathing lights, 3.5mm AUX jack, volume controller pod, and 10W RMS audio.', 849.00, 'ZEB-WAR-SPK-RGB', 'https://images.unsplash.com/photo-1545454675-3531b543be5d?w=600', 1);

-- CATEGORY 12: Traditional Indian Wear (Seller: Meera Singhania, user_id=1002)
INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5004, 1002, 12, 'Pure Kanjivaram Silk Saree (Crimson Red & Gold Zari)', 'Handwoven pure mulberry silk saree featuring traditional temple borders and intricate gold zari pallu. Includes unstitched blouse piece.', 6499.00, 'KANJ-SILK-RED-01', 'https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5005, 1002, 12, 'Lucknowi Chikankari Men Cotton Kurta (Pastel Mint)', 'Breathable premium cambric cotton straight kurta with hand-embroidered floral Chikankari motifs. Tailored for comfort and festive elegance.', 2199.00, 'LUCK-KRT-MNT-42', 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5010, 1002, 12, 'Banarasi Brocade Semi-Stitched Lehenga Choli (Royal Blue)', 'Exquisite woven zari work semi-stitched lehenga with floral border, heavy art-silk blouse piece, and embellished organza dupatta.', 5899.00, 'BANA-LEH-BLU-02', 'https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5011, 1002, 12, 'Jaipuri Bandhani Pure Mulmul Cotton Anarkali Kurti', '100% pure mulmul cotton flared Anarkali kurta featuring authentic tie-and-dye Bandhej craft from Rajasthan with Gota Patti border.', 1850.00, 'JAIP-BAND-KUR-38', 'https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5012, 1002, 12, 'Royal Jodhpuri Bandhgala Silk Blend Blazer (Jet Black)', 'Tailored slim-fit mandarin collar royal Jodhpuri jacket with engraved antique metallic buttons, welt pockets, and satin lining.', 4499.00, 'JODH-BND-BLK-40', 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5013, 1002, 12, 'Chanderi Handloom Festive Saree with Golden Zari Border', 'Lightweight sheer Chanderi cotton-silk drape crafted by Madhya Pradesh weavers with Ashavali bootis and golden zari edging.', 3299.00, 'CHAN-RES-GLD-05', 'https://images.unsplash.com/photo-1610030469668-932ec596e73c?w=600', 1);

-- CATEGORY 13: Spices & Gourmet (Seller: Rajesh Sharma, user_id=1001)
INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5014, 1001, 13, 'Wayanad Organic Whole Black Pepper (Malabar Gold Grade A)', 'Sun-dried bold black peppercorns from Wayanad, Kerala. Rich in piperine with intense aroma and authentic pungency. 500g pouch.', 480.00, 'WAYA-PEP-500G-01', 'https://images.unsplash.com/photo-1599940824399-b87987ceb72a?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5015, 1001, 13, 'Kashmiri Mongra Saffron (Pure Kesar, Grade 1 A++)', 'Pampore origin 100% pure all-red stigma tips. ISO 3632 grade saffron for rich crimson color and culinary fragrance. 1g blister.', 950.00, 'KASH-SAF-1G-01', 'https://images.unsplash.com/photo-1615485500704-8e990f9900f7?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5016, 1001, 13, 'Guntur Teja Dry Red Chillies (Stemless, Extra Spicy)', 'Origin Guntur, Andhra Pradesh. High pungency, bright fiery red color, ideal for sambar and tadka. 500g moisture-lock pack.', 290.00, 'GUNT-CHL-500G-02', 'https://images.unsplash.com/photo-1588252303782-cb80119abd6d?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5017, 1001, 13, 'Coorg Green Cardamom (8mm Jumbo Alleppey Extra Bold)', 'Harvested from plantations in Coorg, Karnataka. Extra bold aromatic pods bursting with sweet herbal oils. 250g pouch.', 720.00, 'COOR-ELA-250G-03', 'https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5018, 1001, 13, 'Salem Turmeric Finger (High Curcumin 5% Unpolished)', 'Naturally golden dried whole turmeric fingers from Salem, Tamil Nadu. Pesticide-free, naturally cured. 500g eco pack.', 240.00, 'SALM-HLD-500G-04', 'https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5019, 1001, 13, 'Darjeeling First Flush Whole Leaf Black Tea (Estate Reserve)', 'Single estate spring harvest tea plucked from high-altitude slopes of Darjeeling. Delicate floral notes, amber liquor. 200g tin.', 850.00, 'DARJ-TEA-200G-05', 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=600', 1);

-- CATEGORY 14: Handicrafts & Home Decor (Seller: Meera Singhania, user_id=1002)
INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5006, 1002, 14, 'Jaipur Blue Pottery Handcrafted Ceramic Flower Vase', 'Traditional quartz and glass fired blue pottery from Jaipur artisans with Turkish floral motifs. Glazed finish, height 10 inches.', 1150.00, 'JAIP-POT-VAS-10', 'https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5020, 1002, 14, 'Moradabad Brass Peacock Diya Oil Lamp (Set of 2)', 'Traditional handcrafted solid brass diyas featuring engraved peacock crests and multi-tier wicks. Antique golden finish for decor.', 1450.00, 'MORA-BRS-DYA-02', 'https://images.unsplash.com/photo-1605371924599-2d0365da1ae0?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5021, 1002, 14, 'Saharanpur Hand-Carved Sheesham Wood Jewelry Box', 'Artisan carved solid Indian rosewood keepsake box with floral jaali filigree lid, brass latch, and royal red velvet lining. 8x5x3 inches.', 899.00, 'SAHR-WD-JWL-BOX', 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5022, 1002, 14, 'Bastar Iron Craft Tribal Musician Figurine (Dokra Art)', 'Authentic lost-wax cast bell metal sculpture created by tribal artisans of Chhattisgarh. Depicts folk musicians celebrating harvest.', 1299.00, 'BAST-IRN-TRB-FIG', 'https://images.unsplash.com/photo-1582562124811-c09040d0a901?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5023, 1002, 14, 'Kashmir Hand-Embroidered Crewel Wool Cushion Covers (Pair)', 'Dense wool chain stitch Aari needlework on unbleached heavy cotton duck fabric. Kashmiri chinar leaf and floral tapestry. 16x16 inches.', 1199.00, 'KASH-CRW-CSH-16', 'https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?w=600', 1);

INSERT INTO products (product_id, seller_id, category_id, name, description, price, sku, image_url, is_active)
VALUES (5024, 1002, 14, 'Terracotta Hand-Painted Warli Art Table Lamp', 'Earthy clay pot lamp handcrafted by rural Maharashtra potters, hand-painted with tribal Warli celebration motifs. Height 12 inches.', 980.00, 'WARL-TER-LMP-01', 'https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=600', 1);

-- 6. Insert Inventory (Stock & Low-Stock Alert Thresholds for all 24 products)
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5001, 5001, 40, 5, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5002, 5002, 25, 5, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5003, 5003, 3, 5, CURRENT_TIMESTAMP); -- LOW STOCK
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5007, 5007, 35, 5, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5008, 5008, 20, 4, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5009, 5009, 15, 3, CURRENT_TIMESTAMP);

INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5004, 5004, 12, 3, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5005, 5005, 18, 4, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5010, 5010, 10, 2, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5011, 5011, 22, 4, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5012, 5012, 14, 3, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5013, 5013, 16, 3, CURRENT_TIMESTAMP);

INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5014, 5014, 50, 8, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5015, 5015, 30, 5, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5016, 5016, 40, 6, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5017, 5017, 28, 5, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5018, 5018, 45, 7, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5019, 5019, 25, 4, CURRENT_TIMESTAMP);

INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5006, 5006, 2, 4, CURRENT_TIMESTAMP); -- LOW STOCK
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5020, 5020, 18, 3, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5021, 5021, 20, 4, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5022, 5022, 8, 2, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5023, 5023, 15, 3, CURRENT_TIMESTAMP);
INSERT INTO inventory (inventory_id, product_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES (5024, 5024, 12, 3, CURRENT_TIMESTAMP);

-- 7. Insert Active Cart Items (Customer: Priya Nair, user_id=1004)
INSERT INTO cart_items (cart_item_id, user_id, product_id, quantity)
VALUES (1, 1004, 5001, 1);

INSERT INTO cart_items (cart_item_id, user_id, product_id, quantity)
VALUES (2, 1004, 5005, 1);

-- 8. Insert Orders & Order Items
-- Order 1: Aarav Patel (Completed/Delivered)
INSERT INTO orders (order_id, order_number, customer_id, total_amount, order_status, shipping_address, payment_method, payment_status, created_at)
VALUES (
    10001, 'ORD-20261110-8841', 1003, 3498.00, 'DELIVERED', 
    'Aarav Patel, Flat 402, Nilgiri Heights, Vastrapur, Ahmedabad, Gujarat - 380015. Mob: +919988776655', 
    'UPI_GPAY', 'PAID', 
    TIMESTAMP '2026-11-10 11:30:00'
);

INSERT INTO order_items (order_item_id, order_id, product_id, seller_id, quantity, unit_price, subtotal)
VALUES (100001, 10001, 5001, 1001, 1, 1999.00, 1999.00);

INSERT INTO order_items (order_item_id, order_id, product_id, seller_id, quantity, unit_price, subtotal)
VALUES (100002, 10001, 5002, 1001, 1, 1499.00, 1499.00);

-- Order 2: Rohan Gupta (Shipped)
INSERT INTO orders (order_id, order_number, customer_id, total_amount, order_status, shipping_address, payment_method, payment_status, created_at)
VALUES (
    10002, 'ORD-20261115-9214', 1005, 6499.00, 'SHIPPED', 
    'Rohan Gupta, B-28, Hazratganj Main Road, Lucknow, Uttar Pradesh - 226001. Mob: +919721004455', 
    'NET_BANKING_HDFC', 'PAID', 
    TIMESTAMP '2026-11-15 16:45:00'
);

INSERT INTO order_items (order_item_id, order_id, product_id, seller_id, quantity, unit_price, subtotal)
VALUES (100003, 10002, 5004, 1002, 1, 6499.00, 6499.00);

-- Order 3: Aarav Patel (New Order Placed)
INSERT INTO orders (order_id, order_number, customer_id, total_amount, order_status, shipping_address, payment_method, payment_status, created_at)
VALUES (
    10003, 'ORD-20261118-1052', 1003, 2300.00, 'PLACED', 
    'Aarav Patel, Flat 402, Nilgiri Heights, Vastrapur, Ahmedabad, Gujarat - 380015. Mob: +919988776655', 
    'UPI_PHONEPE', 'PAID', 
    TIMESTAMP '2026-11-18 10:15:00'
);

INSERT INTO order_items (order_item_id, order_id, product_id, seller_id, quantity, unit_price, subtotal)
VALUES (100004, 10003, 5006, 1002, 2, 1150.00, 2300.00);

COMMIT;
