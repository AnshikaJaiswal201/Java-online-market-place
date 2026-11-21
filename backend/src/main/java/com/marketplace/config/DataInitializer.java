package com.marketplace.config;

import com.marketplace.common.Constants;
import com.marketplace.modules.auth.entity.Role;
import com.marketplace.modules.auth.entity.User;
import com.marketplace.modules.auth.repository.RoleRepository;
import com.marketplace.modules.auth.repository.UserRepository;
import com.marketplace.modules.inventory.entity.Inventory;
import com.marketplace.modules.inventory.repository.InventoryRepository;
import com.marketplace.modules.order.entity.CartItem;
import com.marketplace.modules.order.entity.Order;
import com.marketplace.modules.order.entity.OrderItem;
import com.marketplace.modules.order.repository.CartItemRepository;
import com.marketplace.modules.order.repository.OrderRepository;
import com.marketplace.modules.product.entity.Category;
import com.marketplace.modules.product.entity.Product;
import com.marketplace.modules.product.repository.CategoryRepository;
import com.marketplace.modules.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           InventoryRepository inventoryRepository,
                           CartItemRepository cartItemRepository,
                           OrderRepository orderRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0 && userRepository.count() > 0 && productRepository.count() >= 24) {
            return; // Data already exists in persistent database
        }

        // 1. Roles
        Role customerRole = roleRepository.findByRoleName(Constants.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(Constants.ROLE_CUSTOMER, "Standard Customer Role")));
        Role sellerRole = roleRepository.findByRoleName(Constants.ROLE_SELLER)
                .orElseGet(() -> roleRepository.save(new Role(Constants.ROLE_SELLER, "Merchant / Seller Role")));

        // 2. Users (Default password: Password@123)
        String encodedPassword = passwordEncoder.encode("Password@123");

        // Seller 1: Rajesh Sharma
        User seller1 = new User();
        seller1.setEmail("seller.sharma@marketplace.in");
        seller1.setPassword(encodedPassword);
        seller1.setFullName("Rajesh Sharma");
        seller1.setPhoneNumber("+919876543210");
        seller1.setAddressLine1("Shop 42, SP Road Electronics Complex");
        seller1.setCity("Bengaluru");
        seller1.setState("Karnataka");
        seller1.setPinCode("560002");
        seller1.setIsActive(1);
        Set<Role> s1Roles = new HashSet<>();
        s1Roles.add(sellerRole);
        seller1.setRoles(s1Roles);
        seller1 = userRepository.save(seller1);

        // Seller 2: Meera Singhania
        User seller2 = new User();
        seller2.setEmail("seller.meera@marketplace.in");
        seller2.setPassword(encodedPassword);
        seller2.setFullName("Meera Singhania");
        seller2.setPhoneNumber("+919811223344");
        seller2.setAddressLine1("Plot 18, Johari Bazaar Weavers Lane");
        seller2.setCity("Jaipur");
        seller2.setState("Rajasthan");
        seller2.setPinCode("302003");
        seller2.setIsActive(1);
        Set<Role> s2Roles = new HashSet<>();
        s2Roles.add(sellerRole);
        seller2.setRoles(s2Roles);
        seller2 = userRepository.save(seller2);

        // Customer 1: Aarav Patel
        User customer1 = new User();
        customer1.setEmail("aarav.patel@gmail.com");
        customer1.setPassword(encodedPassword);
        customer1.setFullName("Aarav Patel");
        customer1.setPhoneNumber("+919988776655");
        customer1.setAddressLine1("Flat 402, Nilgiri Heights, Vastrapur");
        customer1.setCity("Ahmedabad");
        customer1.setState("Gujarat");
        customer1.setPinCode("380015");
        customer1.setIsActive(1);
        Set<Role> c1Roles = new HashSet<>();
        c1Roles.add(customerRole);
        customer1.setRoles(c1Roles);
        customer1 = userRepository.save(customer1);

        // Customer 2: Priya Nair
        User customer2 = new User();
        customer2.setEmail("priya.nair@yahoo.co.in");
        customer2.setPassword(encodedPassword);
        customer2.setFullName("Priya Nair");
        customer2.setPhoneNumber("+919447112233");
        customer2.setAddressLine1("12/450, Rose Gardens, Panampilly Nagar");
        customer2.setCity("Kochi");
        customer2.setState("Kerala");
        customer2.setPinCode("682036");
        customer2.setIsActive(1);
        Set<Role> c2Roles = new HashSet<>();
        c2Roles.add(customerRole);
        customer2.setRoles(c2Roles);
        customer2 = userRepository.save(customer2);

        // 3. Categories
        Category catElectronics = categoryRepository.save(new Category("Electronics & Gadgets", "Audio accessories, smart wearables, and mobile peripherals"));
        Category catEthnicWear = categoryRepository.save(new Category("Traditional Indian Wear", "Authentic handloom sarees, kurtas, and festive ethnic clothing"));
        Category catSpices = categoryRepository.save(new Category("Spices & Gourmet", "Farm-fresh organic spices and wellness essentials from South India"));
        Category catDecor = categoryRepository.save(new Category("Handicrafts & Home Decor", "Handcrafted pottery, brass lamps, and artisanal artifacts"));

        // 4. Products & Inventory (6 items per category for rich catalog variety)
        // =========================================================================
        // CATEGORY 1: Electronics & Gadgets (Seller 1: Rajesh Sharma)
        // =========================================================================
        Product p1 = createProduct(seller1, catElectronics, "boAt Rockerz 550 Wireless Bluetooth Headphones",
                "Over-ear wireless headphones with 50mm dynamic drivers, up to 20 hours of continuous playback, physical noise isolation, and signature extra bass tuned for Indian music lovers.",
                new BigDecimal("1999.00"), "BOAT-ROC-550-BLK", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600", 40, 5);

        Product p2 = createProduct(seller1, catElectronics, "Noise ColorFit Pulse Grand Smartwatch",
                "1.69 inch HD display smartwatch with 60 sports modes, SpO2 & 24x7 heart rate monitoring, fast charging, and 150+ cloud watch faces.",
                new BigDecimal("1499.00"), "NOIS-CFP-GRD-BLU", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600", 25, 5);

        createProduct(seller1, catElectronics, "Portronics Konnect L 1.2M Fast Charging Cable",
                "Tangle-free braided Type-C to Lightning 20W PD cable with reinforced aluminum connectors for heavy daily commuting.",
                new BigDecimal("299.00"), "PORT-CB-LGT-20W", "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=600", 3, 5);

        createProduct(seller1, catElectronics, "OnePlus Bullets Z2 Wireless Bluetooth Earphones",
                "Acoustically tuned magnetic neckband with 12.4mm deep bass drivers, 10-minute fast charging for 20 hours playback, and IP55 sweat and water resistance.",
                new BigDecimal("1699.00"), "OP-BUL-Z2-BLK", "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600", 35, 5);

        createProduct(seller1, catElectronics, "Mi 10000mAh Power Bank 3i (18W Fast Charging)",
                "Dual output USB-A ports, dual input Type-C and Micro-USB, 12 layers advanced circuit protection, and smart power management for mobile devices.",
                new BigDecimal("1299.00"), "MI-PB-10K-BLK", "https://images.unsplash.com/photo-1609592424364-e4612d1b7a24?w=600", 20, 4);

        createProduct(seller1, catElectronics, "Zebronics Zeb-Warrior 2.0 RGB Desktop Speakers",
                "Compact USB powered desktop speakers with vibrant RGB breathing lights, 3.5mm AUX jack, volume controller pod, and 10W RMS balanced audio.",
                new BigDecimal("849.00"), "ZEB-WAR-SPK-RGB", "https://images.unsplash.com/photo-1545454675-3531b543be5d?w=600", 15, 3);

        // =========================================================================
        // CATEGORY 2: Traditional Indian Wear (Seller 2: Meera Singhania)
        // =========================================================================
        createProduct(seller2, catEthnicWear, "Pure Kanjivaram Silk Saree (Crimson Red & Gold Zari)",
                "Handwoven pure mulberry silk saree featuring traditional temple borders and intricate gold zari pallu. Ideal for weddings and Diwali celebrations. Includes unstitched blouse piece.",
                new BigDecimal("6499.00"), "KANJ-SILK-RED-01", "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600", 12, 3);

        Product p5 = createProduct(seller2, catEthnicWear, "Lucknowi Chikankari Men Cotton Kurta (Pastel Mint)",
                "Breathable premium cambric cotton straight kurta with hand-embroidered floral Chikankari motifs. Tailored for comfort and festive elegance.",
                new BigDecimal("2199.00"), "LUCK-KRT-MNT-42", "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600", 18, 4);

        createProduct(seller2, catEthnicWear, "Banarasi Brocade Semi-Stitched Lehenga Choli (Royal Blue)",
                "Exquisite woven zari work semi-stitched lehenga with floral border, heavy art-silk blouse piece, and embellished organza dupatta with latkan tassels.",
                new BigDecimal("5899.00"), "BANA-LEH-BLU-02", "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?w=600", 10, 2);

        createProduct(seller2, catEthnicWear, "Jaipuri Bandhani Pure Mulmul Cotton Anarkali Kurti",
                "100% pure mulmul cotton flared Anarkali kurta featuring authentic tie-and-dye Bandhej craft from Rajasthan with Gota Patti border styling.",
                new BigDecimal("1850.00"), "JAIP-BAND-KUR-38", "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?w=600", 22, 4);

        createProduct(seller2, catEthnicWear, "Royal Jodhpuri Bandhgala Silk Blend Blazer (Jet Black)",
                "Tailored slim-fit mandarin collar royal Jodhpuri jacket with engraved antique metallic buttons, welt pockets, and premium satin inner lining.",
                new BigDecimal("4499.00"), "JODH-BND-BLK-40", "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=600", 14, 3);

        createProduct(seller2, catEthnicWear, "Chanderi Handloom Festive Saree with Golden Zari Border",
                "Lightweight sheer Chanderi cotton-silk drape crafted by Madhya Pradesh weavers, adorned with traditional Ashavali bootis and golden zari edging.",
                new BigDecimal("3299.00"), "CHAN-RES-GLD-05", "https://images.unsplash.com/photo-1610030469668-932ec596e73c?w=600", 16, 3);

        // =========================================================================
        // CATEGORY 3: Spices & Gourmet (Seller 1: Rajesh Sharma)
        // =========================================================================
        createProduct(seller1, catSpices, "Wayanad Organic Whole Black Pepper (Malabar Gold Grade A)",
                "Sun-dried bold black peppercorns grown in the misty slopes of Wayanad, Kerala. Rich in natural piperine with intense aroma and authentic pungency. 500g vacuum pouch.",
                new BigDecimal("480.00"), "WAYA-PEP-500G-01", "https://images.unsplash.com/photo-1599940824399-b87987ceb72a?w=600", 50, 8);

        createProduct(seller1, catSpices, "Kashmiri Mongra Saffron (Pure Kesar, Grade 1 A++)",
                "Pampore origin 100% pure all-red stigma tips. Lab-tested ISO 3632 grade saffron known for rich crimson color, natural fragrance, and deep culinary flavor. 1g airtight blister.",
                new BigDecimal("950.00"), "KASH-SAF-1G-01", "https://images.unsplash.com/photo-1615485500704-8e990f9900f7?w=600", 30, 5);

        createProduct(seller1, catSpices, "Guntur Teja Dry Red Chillies (Stemless, Extra Spicy)",
                "Origin Guntur, Andhra Pradesh. High pungency, bright fiery red color, ideal for sambar, tadka, and traditional curry pastes. 500g moisture-lock pack.",
                new BigDecimal("290.00"), "GUNT-CHL-500G-02", "https://images.unsplash.com/photo-1588252303782-cb80119abd6d?w=600", 40, 6);

        createProduct(seller1, catSpices, "Coorg Green Cardamom (8mm Jumbo Alleppey Extra Bold)",
                "Harvested from shaded plantations in Coorg, Karnataka. Extra bold aromatic pods bursting with sweet herbal essential oils. 250g sealable pouch.",
                new BigDecimal("720.00"), "COOR-ELA-250G-03", "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=600", 28, 5);

        createProduct(seller1, catSpices, "Salem Turmeric Finger (High Curcumin 5% Unpolished)",
                "Naturally golden dried whole turmeric fingers from Salem, Tamil Nadu. Pesticide-free, naturally cured without added chemicals. 500g eco pack.",
                new BigDecimal("240.00"), "SALM-HLD-500G-04", "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600", 45, 7);

        createProduct(seller1, catSpices, "Darjeeling First Flush Whole Leaf Black Tea (Estate Reserve)",
                "Single estate spring harvest tea plucked from high-altitude slopes of Darjeeling. Delicate floral muscatel notes, amber liquor, and lingering sweet finish. 200g tin caddy.",
                new BigDecimal("850.00"), "DARJ-TEA-200G-05", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=600", 25, 4);

        // =========================================================================
        // CATEGORY 4: Handicrafts & Home Decor (Seller 2: Meera Singhania)
        // =========================================================================
        Product p6 = createProduct(seller2, catDecor, "Jaipur Blue Pottery Handcrafted Ceramic Flower Vase",
                "Traditional quartz and glass fired blue pottery from Jaipur artisans with Turkish floral motifs. Glazed finish, height 10 inches.",
                new BigDecimal("1150.00"), "JAIP-POT-VAS-10", "https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=600", 2, 4);

        createProduct(seller2, catDecor, "Moradabad Brass Peacock Diya Oil Lamp (Set of 2)",
                "Traditional handcrafted solid brass diyas featuring engraved peacock crests and multi-tier wicks. Antique golden lacquer finish for pooja and festive decor.",
                new BigDecimal("1450.00"), "MORA-BRS-DYA-02", "https://images.unsplash.com/photo-1605371924599-2d0365da1ae0?w=600", 18, 3);

        createProduct(seller2, catDecor, "Saharanpur Hand-Carved Sheesham Wood Jewelry Box",
                "Artisan carved solid Indian rosewood keepsake box with floral jaali filigree lid, brass latch, and royal red velvet lining. Dimensions: 8x5x3 inches.",
                new BigDecimal("899.00"), "SAHR-WD-JWL-BOX", "https://images.unsplash.com/photo-1544816155-12df9643f363?w=600", 20, 4);

        createProduct(seller2, catDecor, "Bastar Iron Craft Tribal Musician Figurine (Dokra Art)",
                "Authentic lost-wax cast bell metal sculpture created by tribal artisans of Chhattisgarh. Depicts folk musicians celebrating rural harvest rituals.",
                new BigDecimal("1299.00"), "BAST-IRN-TRB-FIG", "https://images.unsplash.com/photo-1582562124811-c09040d0a901?w=600", 8, 2);

        createProduct(seller2, catDecor, "Kashmir Hand-Embroidered Crewel Wool Cushion Covers (Pair)",
                "Dense wool chain stitch Aari needlework on unbleached heavy cotton duck fabric. Intricate Kashmiri chinar leaf and floral tapestry. 16x16 inches.",
                new BigDecimal("1199.00"), "KASH-CRW-CSH-16", "https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?w=600", 15, 3);

        createProduct(seller2, catDecor, "Terracotta Hand-Painted Warli Art Table Lamp",
                "Earthy clay pot lamp handcrafted by rural Maharashtra potters, hand-painted with tribal Warli celebration motifs in natural white pigment. Height 12 inches.",
                new BigDecimal("980.00"), "WARL-TER-LMP-01", "https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=600", 12, 3);

        // 5. Pre-seed Cart for Priya Nair
        cartItemRepository.save(new CartItem(customer2, p1, 1));
        cartItemRepository.save(new CartItem(customer2, p5, 1));

        // 6. Pre-seed Orders
        Order order1 = new Order();
        order1.setOrderNumber("ORD-20261110-8841");
        order1.setCustomer(customer1);
        order1.setTotalAmount(new BigDecimal("3498.00"));
        order1.setOrderStatus(Constants.ORDER_STATUS_DELIVERED);
        order1.setShippingAddress("Aarav Patel, Flat 402, Nilgiri Heights, Vastrapur, Ahmedabad, Gujarat - 380015");
        order1.setPaymentMethod("UPI_GPAY");
        order1.setPaymentStatus(Constants.PAYMENT_STATUS_PAID);

        OrderItem oi1 = new OrderItem(order1, p1, seller1, 1, new BigDecimal("1999.00"), new BigDecimal("1999.00"));
        OrderItem oi2 = new OrderItem(order1, p2, seller1, 1, new BigDecimal("1499.00"), new BigDecimal("1499.00"));
        order1.addItem(oi1);
        order1.addItem(oi2);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setOrderNumber("ORD-20261118-1052");
        order2.setCustomer(customer1);
        order2.setTotalAmount(new BigDecimal("2300.00"));
        order2.setOrderStatus(Constants.ORDER_STATUS_PLACED);
        order2.setShippingAddress("Aarav Patel, Flat 402, Nilgiri Heights, Vastrapur, Ahmedabad, Gujarat - 380015");
        order2.setPaymentMethod("UPI_PHONEPE");
        order2.setPaymentStatus(Constants.PAYMENT_STATUS_PAID);

        OrderItem oi3 = new OrderItem(order2, p6, seller2, 2, new BigDecimal("1150.00"), new BigDecimal("2300.00"));
        order2.addItem(oi3);
        orderRepository.save(order2);
    }

    private Product createProduct(User seller, Category category, String name, String description,
                                  BigDecimal price, String sku, String imageUrl, int initialStock, int lowStockThreshold) {
        Product product = new Product();
        product.setSeller(seller);
        product.setCategory(category);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setSku(sku);
        product.setImageUrl(imageUrl);
        product.setIsActive(1);
        Product savedProduct = productRepository.save(product);

        Inventory inventory = new Inventory(savedProduct, initialStock, lowStockThreshold);
        inventoryRepository.save(inventory);

        return savedProduct;
    }
}
