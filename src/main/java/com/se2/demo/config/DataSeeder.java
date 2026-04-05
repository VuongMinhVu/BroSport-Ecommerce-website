package com.se2.demo.config;

import com.se2.demo.dto.async.ProductEvent;
import com.se2.demo.mapper.ProductMapper;
import com.se2.demo.model.entity.*;
import com.se2.demo.repository.*;
import com.se2.demo.utils.constant.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {
        private final ApplicationEventPublisher publisher;
        private final ProductMapper productMapper;

        @Bean
        public CommandLineRunner initDatabase(
                        CategoryRepository categoryRepository,
                        BrandRepository brandRepository,
                        ColorRepository colorRepository,
                        SizeRepository sizeRepository,
                        GenderRepository genderRepository,
                        SportRepository sportRepository,
                        ProductRepository productRepository,
                        ProductDetailRepository productDetailRepository,
                        ProductImageRepository productImageRepository,
                        UserRepository userRepository,
                        OrderRepository orderRepository,
                        CartRepository cartRepository,
                        CartDetailRepository cartDetailRepository,
                        ReviewRepository reviewRepository,
                        PasswordEncoder passwordEncoder) {

                return args -> {
                        log.info("Checking database for seed data...");

                        if (productRepository.count() >= 100) {
                                log.info("Database already seeded with enough products.");
                                List<Product> existingProducts = productRepository.findAll();
                                boolean needUpdate = false;
                                for (Product p : existingProducts) {
                                        if (p.getOriginPrice() != null && p.getOriginPrice()
                                                        .compareTo(BigDecimal.valueOf(10000)) > 0) {
                                                p.setOriginPrice(p.getOriginPrice().divide(BigDecimal.valueOf(10000)));
                                                p.setShowPrice(p.getShowPrice().divide(BigDecimal.valueOf(10000)));
                                                needUpdate = true;
                                        }
                                }
                                if (needUpdate) {
                                        productRepository.saveAll(existingProducts);
                                        log.info("Scaled down all high product prices to match new format.");
                                }
                                return;
                        }

                        log.info("Starting to seed database...");

                        // 1. Seed Brands
                        List<Brand> brands = new ArrayList<>();
                        if (brandRepository.count() == 0) {
                                brands = Arrays.asList(
                                                Brand.builder().name("Nike")
                                                                .logoUrl("https://picsum.photos/seed/nike/400/400")
                                                                .description("Just do it").build(),
                                                Brand.builder().name("Adidas")
                                                                .logoUrl("https://picsum.photos/seed/adidas/400/400")
                                                                .description("Impossible is nothing")
                                                                .build(),
                                                Brand.builder().name("Puma")
                                                                .logoUrl("https://picsum.photos/seed/puma/400/400")
                                                                .description("Forever Faster").build(),
                                                Brand.builder().name("Under Armour")
                                                                .logoUrl("https://picsum.photos/seed/ua/400/400")
                                                                .description("Protect This House")
                                                                .build(),
                                                Brand.builder().name("New Balance")
                                                                .logoUrl("https://picsum.photos/seed/nb/400/400")
                                                                .description("Fearlessly Independent")
                                                                .build());
                                brandRepository.saveAll(brands);
                        } else {
                                brands = brandRepository.findAll();
                        }

                        // 2. Seed Categories
                        List<Category> leafCategories = new ArrayList<>();
                        if (categoryRepository.count() == 0) {
                                Category shoes = Category.builder().name("Sports Shoes").slug("sports-shoes")
                                                .imageUrl("https://picsum.photos/seed/shoes/600/600")
                                                .sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg")
                                                .build();
                                Category clothing = Category.builder().name("Apparel").slug("apparel")
                                                .imageUrl("https://picsum.photos/seed/clothes/600/600")
                                                .sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg")
                                                .build();
                                Category accessories = Category.builder().name("Accessories").slug("accessories")
                                                .imageUrl("https://picsum.photos/seed/accessories/600/600")
                                                .sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg")
                                                .build();
                                categoryRepository.saveAll(Arrays.asList(shoes, clothing, accessories));

                                Category runningShoes = Category.builder().name("Running Shoes").slug("running-shoes")
                                                .parentCategory(shoes).sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg").build();
                                Category footballShoes = Category.builder().name("Football Shoes").slug("football-shoes")
                                                .parentCategory(shoes).sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg").build();
                                Category basketballShoes = Category.builder().name("Basketball Shoes").slug("basketball-shoes")
                                                .parentCategory(shoes).sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg").build();

                                Category tshirts = Category.builder().name("T-Shirts").slug("t-shirts")
                                                .parentCategory(clothing).sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg").build();
                                Category pants = Category.builder().name("Pants").slug("pants")
                                                .parentCategory(clothing).sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg").build();
                                Category shorts = Category.builder().name("Shorts").slug("shorts")
                                                .parentCategory(clothing).sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg")
                                                .build();

                                categoryRepository
                                                .saveAll(Arrays.asList(runningShoes, footballShoes, basketballShoes,
                                                                tshirts, pants, shorts));
                                leafCategories = Arrays.asList(runningShoes, footballShoes, basketballShoes, tshirts,
                                                pants, shorts);
                        } else {
                                leafCategories = categoryRepository.findAll().stream()
                                                .filter(c -> c.getParentCategory() != null)
                                                .toList();
                        }

                        // 3. Seed Colors
                        List<Color> colors = new ArrayList<>();
                        if (colorRepository.count() == 0) {
                                colors = Arrays.asList(
                                                Color.builder().colorName("Black").build(),
                                                Color.builder().colorName("White").build(),
                                                Color.builder().colorName("Red").build(),
                                                Color.builder().colorName("Blue").build(),
                                                Color.builder().colorName("Grey").build(),
                                                Color.builder().colorName("Yellow").build());
                                colorRepository.saveAll(colors);
                        } else {
                                colors = colorRepository.findAll();
                        }

                        // 4. Seed Sizes
                        List<Size> sizes = new ArrayList<>();
                        if (sizeRepository.count() == 0) {
                                sizes = Arrays.asList(
                                                Size.builder().sizeDescription("38").build(),
                                                Size.builder().sizeDescription("39").build(),
                                                Size.builder().sizeDescription("40").build(),
                                                Size.builder().sizeDescription("41").build(),
                                                Size.builder().sizeDescription("42").build(),
                                                Size.builder().sizeDescription("43").build(),
                                                Size.builder().sizeDescription("S").build(),
                                                Size.builder().sizeDescription("M").build(),
                                                Size.builder().sizeDescription("L").build(),
                                                Size.builder().sizeDescription("XL").build());
                                sizeRepository.saveAll(sizes);
                        } else {
                                sizes = sizeRepository.findAll();
                        }

                        List<Size> shoeSizes = sizes.stream()
                                        .filter(s -> s.getSizeDescription().matches("\\d+"))
                                        .toList();
                        List<Size> clothingSizes = sizes.stream()
                                        .filter(s -> !s.getSizeDescription().matches("\\d+"))
                                        .toList();

                        if (shoeSizes.isEmpty())
                                shoeSizes = sizes;
                        if (clothingSizes.isEmpty())
                                clothingSizes = sizes;

                        // 5. Seed Genders
                        List<Gender> genders = new ArrayList<>();
                        if (genderRepository.count() == 0) {
                                genders = Arrays.asList(
                                                Gender.builder().name("Men").description("Men's products")
                                                                .logoUrl("https://picsum.photos/seed/male/400/400")
                                                                .build(),
                                                Gender.builder().name("Women").description("Women's products")
                                                                .logoUrl("https://picsum.photos/seed/female/400/400")
                                                                .build(),
                                                Gender.builder().name("Kids").description("Kids products")
                                                                .logoUrl("https://picsum.photos/seed/kids/400/400")
                                                                .build());
                                genderRepository.saveAll(genders);
                        } else {
                                genders = genderRepository.findAll();
                        }

                        // 6. Seed Sports
                        List<Sport> sports = new ArrayList<>();
                        if (sportRepository.count() == 0) {
                                sports = Arrays.asList(
                                                Sport.builder().name("Football").description("King of sports")
                                                                .logoUrl("https://picsum.photos/seed/football/400/400")
                                                                .build(),
                                        Sport.builder().name("Running").description("Fitness conditioning")
                                                                .logoUrl("https://picsum.photos/seed/running/400/400")
                                                                .build(),
                                                Sport.builder().name("Basketball").description("Team sports")
                                                                .logoUrl("https://picsum.photos/seed/basketball/400/400")
                                                                .build(),
                                                Sport.builder().name("Gym & Training").description("Weight training")
                                                                .logoUrl("https://picsum.photos/seed/gym/400/400")
                                                                .build(),
                                                Sport.builder().name("Tennis").description("Royal sports")
                                                                .logoUrl("https://picsum.photos/seed/tennis/400/400")
                                                                .build(),
                                                Sport.builder().name("Badminton").description("Popular sports")
                                                                .logoUrl("https://picsum.photos/seed/badminton/400/400")
                                                                .build()
                                );
                                sportRepository.saveAll(sports);
                        } else {
                                sports = sportRepository.findAll();
                        }

                        // 7. Seed Products
                        if (productRepository.count() < 100) {
                                log.info("Seeding 30 Products with variations...");
                                Random random = new Random();

                                for (int i = 1; i <= 100; i++) {
                                        Brand randomBrand = brands.get(random.nextInt(brands.size()));
                                        Category randomCategory = leafCategories.get(random.nextInt(leafCategories.size()));

                                        Gender randomGender = genders.get(random.nextInt(genders.size()));
                                        Sport randomSport = sports.get(random.nextInt(sports.size()));

                                        Map<String, Object> infoMap = new HashMap<>();
                                        infoMap.put("Sport", randomSport.getName());
                                        infoMap.put("Key Features", "Sweat wicking, Soft, Comfortable");
                                        infoMap.put("Fit", "Regular Fit");
                                        infoMap.put("Material", "100% Premium Polyester");

                                        String productName = randomCategory.getName() + " " + randomBrand.getName()
                                                        + " Version " + i;
                                        String slug = "product-" + randomCategory.getSlug() + "-"
                                                        + randomBrand.getName().toLowerCase() + "-"
                                                        + i;

                                        BigDecimal price = BigDecimal.valueOf((random.nextInt(20) + 5) * 100000); 
                                        BigDecimal comparePrice = price.multiply(BigDecimal.valueOf(1.2));

                                        Product product = Product.builder()
                                                        .name(productName)
                                                        .slug(slug)
                                                        .description("Detailed description for " + productName
                                                                        + ". Great product for maximum athletic performance. Breathable and comfortable material.")
                                                        .status("ACTIVE")
                                                        .feature("Highlights: Ultra-lightweight, exclusive "
                                                                        + randomBrand.getName()
                                                                        + " technology offering exceptional daily comfort.")
                                                        .information(infoMap)
                                                        .brand(randomBrand)
                                                        .category(randomCategory)
                                                        .gender(randomGender)
                                                        .sport(randomSport)
                                                        .originPrice(price)
                                                        .showPrice(comparePrice)
                                                        .build();

                                        Product savedProduct = productRepository.save(product);

                                        List<ProductImage> images = new ArrayList<>();
                                        int numImages = random.nextInt(3) + 1; 
                                        for (int imgIdx = 1; imgIdx <= numImages; imgIdx++) {
                                                ProductImage mainImage = ProductImage.builder()
                                                                .product(savedProduct)
                                                                .imageUrl("https://picsum.photos/seed/"
                                                                                + (savedProduct.getId() * 10 + imgIdx)
                                                                                + "/800/800")
                                                                .isMain(imgIdx == 1)
                                                                .sortOrder(imgIdx)
                                                                .build();
                                                productImageRepository.save(mainImage);
                                                images.add(mainImage);
                                        }
                                        savedProduct.setProductImages(images);

                                        List<ProductDetail> details = new ArrayList<>();
                                        int numVariants = random.nextInt(4) + 2; 
                                        Set<String> generatedCombos = new HashSet<>();

                                        for (int v = 0; v < numVariants; v++) {
                                                Color randomColor = colors.get(random.nextInt(colors.size()));
                                                Size randomSize;
                                                if (randomCategory.getName().toLowerCase().contains("shoes")) {
                                                        randomSize = shoeSizes.get(random.nextInt(shoeSizes.size()));
                                                } else {
                                                        randomSize = clothingSizes.get(random.nextInt(clothingSizes.size()));
                                                }

                                                String comboKey = randomColor.getId() + "-" + randomSize.getId();
                                                if (generatedCombos.contains(comboKey)) {
                                                        continue;
                                                }
                                                generatedCombos.add(comboKey);

                                                String sku = "SKU-" + savedProduct.getId() + "-" + randomColor.getId() + "-"
                                                                + randomSize.getId();

                                                ProductDetail detail = ProductDetail.builder()
                                                                .product(savedProduct)
                                                                .color(randomColor)
                                                                .size(randomSize)
                                                                .stockQuantity(random.nextInt(100) + 10)
                                                                .weightInGrams((float) (random.nextInt(500) + 200))
                                                                .sku(sku)
                                                                .build();

                                                productDetailRepository.save(detail);

                                                // Save image variant relation
                                                if (!images.isEmpty()) {
                                                        ProductImage img = images.get(random.nextInt(images.size()));
                                                        img.setVariant(detail);
                                                        productImageRepository.save(img);
                                                }

                                                details.add(detail);
                                        }
                                        savedProduct.setProductDetails(details);

                                        this.publisher.publishEvent(
                                                        new ProductEvent(this.productMapper.toDocument(savedProduct),
                                                                        Constant.PRODUCT_CREATED_EVENT));
                                }

                        }

                        // 8. Seed Users
                        List<User> users = new ArrayList<>();
                        if (userRepository.count() == 0) {
                                log.info("Seeding Users...");
                                String defaultPassword = passwordEncoder.encode("12345");
                                users.add(User.builder().email("admin@brosport.com").passwordHash(defaultPassword).fullName("Admin User").phone("0123456780").role("ADMIN").build());
                                users.add(User.builder().email("customer1@brosport.com").passwordHash(defaultPassword).fullName("John Doe").phone("0123456781").role("USER").build());
                                users.add(User.builder().email("customer2@brosport.com").passwordHash(defaultPassword).fullName("Jane Smith").phone("0123456782").role("USER").build());
                                users.add(User.builder().email("customer3@brosport.com").passwordHash(defaultPassword).fullName("Mike Johnson").phone("0123456783").role("USER").build());
                                users.add(User.builder().email("customer4@brosport.com").passwordHash(defaultPassword).fullName("Emily Davis").phone("0123456784").role("USER").build());
                                users = userRepository.saveAll(users);
                        } else {
                                users = userRepository.findAll();
                        }

                        // 9. Seed Cart, Order, Review
                        List<ProductDetail> allProductDetails = productDetailRepository.findAll();
                        if (!allProductDetails.isEmpty() && users.size() >= 5) {
                                User admin = users.stream().filter(u -> "ADMIN".equalsIgnoreCase(u.getRole())).findFirst().orElse(users.get(0));
                                User customer1 = users.get(1);
                                User customer2 = users.get(2);

                                // Cart & CartDetail
                                if (cartRepository.count() == 0) {
                                        log.info("Seeding Carts...");
                                        Cart cart1 = new Cart();
                                        cart1.setUser(customer1);
                                        cart1.setCreatedAt(LocalDateTime.now());
                                        cart1.setUpdatedAt(LocalDateTime.now());
                                        cartRepository.save(cart1);

                                        CartDetail cd1 = new CartDetail();
                                        cd1.setCart(cart1);
                                        cd1.setProductDetail(allProductDetails.get(0));
                                        cd1.setQuantity(2);
                                        cd1.setCreatedAt(LocalDateTime.now());
                                        cd1.setUpdatedAt(LocalDateTime.now());
                                        cartDetailRepository.save(cd1);

                                        Cart cart2 = new Cart();
                                        cart2.setUser(customer2);
                                        cart2.setCreatedAt(LocalDateTime.now());
                                        cart2.setUpdatedAt(LocalDateTime.now());
                                        cartRepository.save(cart2);
                                }

                                // Order & OrderItem
                                if (orderRepository.count() == 0) {
                                        log.info("Seeding Orders...");
                                        Order order1 = Order.builder()
                                                .orderCode("BS-SEED-" + System.currentTimeMillis())
                                                .user(customer1)
                                                .totalPrice(BigDecimal.valueOf(1000000))
                                                .shippingFee(BigDecimal.valueOf(30000))
                                                .paymentMethod("COD")
                                                .paymentStatus("PAID")
                                                .orderStatus("DELIVERED")
                                                .createdAt(LocalDateTime.now())
                                                .fullName(customer1.getFullName())
                                                .shippingAddressFull("123 Seed Street, City")
                                                .build();
                                                
                                        OrderItem oi1 = OrderItem.builder()
                                                .order(order1)
                                                .productDetail(allProductDetails.get(0))
                                                .quantity(1)
                                                .price(allProductDetails.get(0).getProduct().getShowPrice())
                                                .build();
                                        
                                        OrderItem oi2 = OrderItem.builder()
                                                .order(order1)
                                                .productDetail(allProductDetails.get(1))
                                                .quantity(1)
                                                .price(allProductDetails.get(1).getProduct().getShowPrice())
                                                .build();
                                                
                                        order1.setOrderItems(Arrays.asList(oi1, oi2));
                                        orderRepository.save(order1);

                                        // Second order
                                        Order order2 = Order.builder()
                                                .orderCode("BS-SEED-" + (System.currentTimeMillis() + 1))
                                                .user(customer2)
                                                .totalPrice(BigDecimal.valueOf(500000))
                                                .shippingFee(BigDecimal.valueOf(30000))
                                                .paymentMethod("VNPAY")
                                                .paymentStatus("PAID")
                                                .orderStatus("DELIVERED")
                                                .createdAt(LocalDateTime.now().minusDays(1))
                                                .fullName(customer2.getFullName())
                                                .shippingAddressFull("456 Branch Road, District")
                                                .build();
                                                
                                        OrderItem oi3 = OrderItem.builder()
                                                .order(order2)
                                                .productDetail(allProductDetails.get(2))
                                                .quantity(1)
                                                .price(allProductDetails.get(2).getProduct().getShowPrice())
                                                .build();
                                                
                                        order2.setOrderItems(Arrays.asList(oi3));
                                        orderRepository.save(order2);
                                }

                                // Reviews
                                if (reviewRepository.count() == 0) {
                                        log.info("Seeding Reviews...");
                                        Review review1 = Review.builder()
                                                .user(customer1)
                                                .product(allProductDetails.get(0).getProduct())
                                                .rating(5)
                                                .comment("Great quality! Fits perfectly and very comfortable.")
                                                .createdAt(LocalDateTime.now())
                                                .build();
                                                
                                        reviewRepository.save(review1);
                                        
                                        // Admin replies to review
                                        Review reply1 = Review.builder()
                                                .user(admin)
                                                .product(allProductDetails.get(0).getProduct())
                                                .rating(5) 
                                                .comment("Thank you for your feedback! We are glad you love it.")
                                                .parentReview(review1)
                                                .createdAt(LocalDateTime.now())
                                                .build();
                                                
                                        reviewRepository.save(reply1);

                                        Review review2 = Review.builder()
                                                .user(customer2)
                                                .product(allProductDetails.get(2).getProduct())
                                                .rating(4)
                                                .comment("Good value for the price.")
                                                .createdAt(LocalDateTime.now().minusHours(5))
                                                .build();
                                                
                                        reviewRepository.save(review2);
                                }
                        }

                        log.info("Database seeding completed successfully! Entities inserted.");
                };
        }
}