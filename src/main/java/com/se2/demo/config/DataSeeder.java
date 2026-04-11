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
                        ChatConversationRepository chatConversationRepository,
                        ChatMessageRepository chatMessageRepository,
                        PasswordEncoder passwordEncoder) {

                return args -> {
                        log.info("Checking database for seed data...");

                        if (productRepository.count() >= 40) {
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
                        }

                        log.info("Starting to seed database...");

                        // 1. Seed Brands
                        List<Brand> brands = new ArrayList<>();
                        if (brandRepository.count() == 0) {
                                brands = Arrays.asList(
                                                Brand.builder().name("Nike")
                                                                .logoUrl("//supersports.com.vn/cdn/shop/files/SP_BR_NIKE_c3a940f1-9d42-47a2-bec3-04a47aa62e04.jpg?v=1715040392&width=286")
                                                                .description("Just do it").build(),
                                                Brand.builder().name("Adidas")
                                                                .logoUrl("//supersports.com.vn/cdn/shop/files/SP_BR_DAS_29f333f3-037a-4340-b15c-91e290153a20.jpg?v=1715040392&width=287")
                                                                .description("Impossible is nothing")
                                                                .build(),
                                                Brand.builder().name("Puma")
                                                                .logoUrl("//supersports.com.vn/cdn/shop/files/PUMA_a7d5855f-754a-4f63-a630-fbea0a3822c2.jpg?v=1759909814&width=285")
                                                                .description("Forever Faster").build(),
                                                Brand.builder().name("Under Armour")
                                                                .logoUrl("//supersports.com.vn/cdn/shop/files/SP_BR_UA_971a481e-3d35-4376-b5a3-332e2d34165d.jpg?v=1715040392&width=286")
                                                                .description("Protect This House")
                                                                .build(),
                                                Brand.builder().name("New Balance")
                                                                .logoUrl("//supersports.com.vn/cdn/shop/files/ON_black.jpg?v=1730194141&width=285")
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
                                                .imageUrl("https://cdn.shopify.com/s/files/1/0456/5070/6581/files/1011C223.400-1_900x.jpg?v=1775458989")
                                                .sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg")
                                                .build();
                                Category clothing = Category.builder().name("Apparel").slug("apparel")
                                                .imageUrl("https://supersports.com.vn/cdn/shop/files/1386973-690-1.jpg?v=1773110315&width=1000")
                                                .sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg")
                                                .build();
                                Category accessories = Category.builder().name("Accessories").slug("accessories")
                                                .imageUrl("https://supersports.com.vn/cdn/shop/products/DC4244-010-1.jpg?v=1669803434")
                                                .sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg")
                                                .build();
                                categoryRepository.saveAll(Arrays.asList(shoes, clothing, accessories));

                                Category runningShoes = Category.builder().name("Running Shoes").slug("running-shoes")
                                                .parentCategory(shoes)
                                                .sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg")
                                                .build();
                                Category footballShoes = Category.builder().name("Football Shoes")
                                                .slug("football-shoes")
                                                .parentCategory(shoes)
                                                .sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg")
                                                .build();
                                Category basketballShoes = Category.builder().name("Basketball Shoes")
                                                .slug("basketball-shoes")
                                                .parentCategory(shoes)
                                                .sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg")
                                                .build();

                                Category tshirts = Category.builder().name("T-Shirts").slug("t-shirts")
                                                .parentCategory(clothing)
                                                .sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg")
                                                .build();
                                Category pants = Category.builder().name("Pants").slug("pants")
                                                .parentCategory(clothing)
                                                .sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg")
                                                .build();
                                Category shorts = Category.builder().name("Shorts").slug("shorts")
                                                .parentCategory(clothing)
                                                .sizeGuide("https://d1w6lranmzyrqf.cloudfront.net/uploads/20220507/aca14234870be6b9e87ce7bd3933ed01.jpg")
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
                                                                .logoUrl("//supersports.com.vn/cdn/shop/files/HP_S26_SPORT_FOOT_VN.png?v=1767689298&width=352")
                                                                .build(),
                                                Sport.builder().name("Running").description("Fitness conditioning")
                                                                .logoUrl("//supersports.com.vn/cdn/shop/files/HP_S26_SPORT_RUN_VN.png?v=1767689298&width=352")
                                                                .build(),
                                                Sport.builder().name("Basketball").description("Team sports")
                                                                .logoUrl("//supersports.com.vn/cdn/shop/files/HP_S26_SPORT_BASKET_VN.png?v=1767689298&width=352")
                                                                .build(),
                                                Sport.builder().name("Gym & Training").description("Weight training")
                                                                .logoUrl("//supersports.com.vn/cdn/shop/files/HP_S26_SPORT_FIT_VN.png?v=1767689298&width=352")
                                                                .build(),
                                                Sport.builder().name("Tennis").description("Royal sports")
                                                                .logoUrl("//supersports.com.vn/cdn/shop/files/HP_S26_SPORT_TENNIS_VN.png?v=1767689298&width=352")
                                                                .build(),
                                                Sport.builder().name("Badminton").description("Popular sports")
                                                                .logoUrl("//supersports.com.vn/cdn/shop/files/HP_S26_SPORT_OUTDOOR_VN.png?v=1767689298&width=352")
                                                                .build());
                                sportRepository.saveAll(sports);
                        } else {
                                sports = sportRepository.findAll();
                        }

                        // 7. Seed Products
                        if (productRepository.count() < 40) {
                                log.info("Seeding 30 Products with variations...");
                                Random random = new Random();

                                for (int i = 1; i <= 40; i++) {
                                        Brand randomBrand = brands.get(random.nextInt(brands.size()));
                                        Category randomCategory = leafCategories
                                                        .get(random.nextInt(leafCategories.size()));

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

                                        BigDecimal basePrice = BigDecimal.valueOf(random.nextInt(151) + 50); // $50 to $200
                                        BigDecimal originPrice = basePrice;
                                        BigDecimal showPrice = basePrice;
                                        
                                        // 30% có giá khuyến mại (giá bán thấp hơn giá gốc)
                                        if (random.nextInt(100) < 30) {
                                            originPrice = basePrice.multiply(BigDecimal.valueOf(1.2)); // Tăng giá gốc lên 20%
                                            showPrice = basePrice; // Bán giá hiện tại (giảm giá)
                                        }

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
                                                        .originPrice(originPrice)
                                                        .showPrice(showPrice)
                                                        .build();

                                        Product savedProduct = productRepository.save(product);

                                        List<ProductImage> images = new ArrayList<>();
                                        
                                        List<String> tshirtsImgs = Arrays.asList(
                                            "https://supersports.com.vn/cdn/shop/files/1386973-690-1.jpg?v=1773110315&width=1000",
                                            "https://supersports.com.vn/cdn/shop/files/1386973-338-1.jpg?v=1771835249&width=1000",
                                            "https://supersports.com.vn/cdn/shop/files/2157331010-1.jpg?v=1772595841&width=1000",
                                            "https://supersports.com.vn/cdn/shop/files/DM6428-100-1.jpg?v=1775459418",
                                            "https://supersports.com.vn/cdn/shop/files/IH1956-486-1.jpg?v=1774413021",
                                            "https://supersports.com.vn/cdn/shop/files/KE5325-1.jpg?v=1767952982",
                                            "https://supersports.com.vn/cdn/shop/files/FB7933-010-1.jpg?v=1762772360",
                                            "https://supersports.com.vn/cdn/shop/files/6005932-402-1.jpg?v=1756119434",
                                            "https://supersports.com.vn/cdn/shop/files/IH1156-045-1.jpg?v=1774412971",
                                            "https://supersports.com.vn/cdn/shop/files/IF2198-010-1.jpg?v=1774412855",
                                            "https://supersports.com.vn/cdn/shop/files/IH1956-010-1.jpg?v=1774412998",
                                            "https://supersports.com.vn/cdn/shop/files/1174750-WHT-1.jpg?v=1765361284",
                                            "https://supersports.com.vn/cdn/shop/files/KD6416-1.jpg?v=1772013224"
                                        );
                                        List<String> pantsImgs = Arrays.asList(
                                            "https://n7media.coolmate.me/uploads/December2024/quan-dai-nam-ut-pants-v3-xam-dam_(8).jpg",
                                            "https://n7media.coolmate.me/uploads/November2025/quan-dai-nam-ecc-warp-pant-taper-1-1-xanh-reu.jpg",
                                            "https://n7media.coolmate.me/uploads/October2025/quan-dai-track-pants-winbreaker-cargo-7-xanh-navy_66.jpg",
                                            "https://supersports.com.vn/cdn/shop/products/1370082-001-1.jpg?v=1745835792",
                                            "https://supersports.com.vn/cdn/shop/files/2126402279-1.jpg?v=1730169409",
                                            "https://supersports.com.vn/cdn/shop/files/1388823-348-1.jpg?v=1740971386",
                                            "https://supersports.com.vn/cdn/shop/files/95D313-023-1.jpg?v=1731057562&width=1000",
                                            "https://n7media.coolmate.me/uploads/August2025/quan-nam-travel-shorts-7-inch-xam-123.jpg",
                                            "https://n7media.coolmate.me/uploads/December2024/quan-dai-chay-bo-running-pant-den_(3).jpg",
                                            "https://n7media.coolmate.me/uploads/December2024/quan-dai-nam-woven-excool-sieu-nhe-xanh-navy_(5).jpg"
                                        );
                                        List<String> shortsImgs = Arrays.asList(
                                            "https://n7media.coolmate.me/uploads/August2025/quan-short-ecc-ripstop-1-1-xanh-la-dam.jpg",
                                            "https://n7media.coolmate.me/uploads/September2025/quan-shorts-nam-chay-bo-ultra-fast-free-run-ii-exdry-nhanh-kho-den-1.jpg",
                                            "https://supersports.com.vn/cdn/shop/files/1170236-BLK-1.jpg?v=1753342070",
                                            "https://supersports.com.vn/cdn/shop/files/6007632-600-1.jpg?v=1756119586",
                                            "https://supersports.com.vn/cdn/shop/files/6007632-001-1.jpg?v=1756119809",
                                            "https://n7media.coolmate.me/uploads/November2025/short-the-thao-promax-side-9-trang_60.jpg",
                                            "https://n7media.coolmate.me/uploads/2026/12/27/quan-shorts-chay-trail-widflow-2-9-den_13.jpg",
                                            "https://n7media.coolmate.me/uploads/August2025/quan-chino-nam-7-inch-trang-1-1.jpg",
                                            "https://n7media.coolmate.me/uploads/September2025/quan-shorts-chay-bo-economy-ii-xanh-ngoc-1.jpg"
                                        );
                                        List<String> runningShoesImgs = Arrays.asList(
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/1011C223.400-1_900x.jpg?v=1775458989",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/1171904-SBY-2_900x.jpg?v=1772511793",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/IB1895-101-1_900x.jpg?v=1774410854",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/HM6803-802-1_900x.jpg?v=1774410767",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/JP9192-1_900x.jpg?v=1772012802",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/JS4938-1_900x.jpg?v=1754991362",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/31291301-1_900x.jpg?v=1773816307",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/6006723-101-1_900x.jpg?v=1773110314",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/6006717-003-1_900x.jpg?v=1773110315",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/6006723-299-1_900x.jpg?v=1773110317",
                                            "https://supersports.com.vn/cdn/shop/files/1171930-BFS-1.jpg?v=1775013032&width=1000",
                                            "https://supersports.com.vn/cdn/shop/files/1171930-BWHT-1.jpg?v=1768291962",
                                            "https://supersports.com.vn/cdn/shop/files/JS4938-1.jpg?v=1754991362&width=1000",
                                            "https://supersports.com.vn/cdn/shop/files/2079321101-1.jpg?v=1769661169",
                                            "https://supersports.com.vn/cdn/shop/files/2079321028-1.jpg?v=1722919919&width=1000",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/HM9594-005-2_6bdd8a5d-96d2-493f-bf57-9d977de6f77f_900x.jpg?v=1772781796"
                                        );
                                        List<String> footballShoesImgs = Arrays.asList(
                                            "//supersports.com.vn/cdn/shop/files/IM3646-640-2.jpg?v=1773201614&width=1000",
                                            "//supersports.com.vn/cdn/shop/files/JH8854-2.jpg?v=1751364966&width=1000",
                                            "//supersports.com.vn/cdn/shop/files/JH8853-2.jpg?v=1759117881&width=1000",
                                            "//supersports.com.vn/cdn/shop/files/DV4337-402-1.jpg?v=1757327173&width=1000",
                                            "//supersports.com.vn/cdn/shop/files/DV4337-800-2_6b4e2604-cc89-4824-a709-2f9288cbe306.jpg?v=1770854228&width=1000",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/Q1GB261350-1_900x.jpg?v=1769421326",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/JQ0954-1_900x.jpg?v=1770374706",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/10897501-1_900x.jpg?v=1764737496",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/JI1133-1_900x.jpg?v=1752054004",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/JR8977-1_900x.jpg?v=1767579612"
                                        );
                                        List<String> basketballShoesImgs = Arrays.asList(
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/HV1991-001-1_900x.jpg?v=1765362373",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/HQ8650-076-1_900x.jpg?v=1766657994",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/FB2237-004-1_900x.jpg?v=1725531193",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/HJ6777-018-1_900x.jpg?v=1757328270",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/FB2237-402-1_900x.jpg?v=1728985664",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/HQ4613-001-1_900x.jpg?v=1765510498",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/6001920-001-1_900x.jpg?v=1746669201",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/3027630-001-1_900x.jpg?v=1733987492",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/3028805-803-1_900x.jpg?v=1738661228",
                                            "https://cdn.shopify.com/s/files/1/0456/5070/6581/files/3027633-001-1_900x.jpg?v=1733202053"
                                        );

                                        int numImages = random.nextInt(3) + 1;
                                        for (int imgIdx = 1; imgIdx <= numImages; imgIdx++) {
                                                String selectedImg = "https://picsum.photos/seed/" + (savedProduct.getId() * 10 + imgIdx) + "/800/800";
                                                String catName = randomCategory.getName().toLowerCase();
                                                
                                                if (catName.contains("t-shirt")) {
                                                    selectedImg = tshirtsImgs.get(random.nextInt(tshirtsImgs.size()));
                                                } else if (catName.contains("pant")) {
                                                    selectedImg = pantsImgs.get(random.nextInt(pantsImgs.size()));
                                                } else if (catName.contains("short")) {
                                                    selectedImg = shortsImgs.get(random.nextInt(shortsImgs.size()));
                                                } else if (catName.contains("running")) {
                                                    selectedImg = runningShoesImgs.get(random.nextInt(runningShoesImgs.size()));
                                                } else if (catName.contains("football")) {
                                                    selectedImg = footballShoesImgs.get(random.nextInt(footballShoesImgs.size()));
                                                } else if (catName.contains("basketball")) {
                                                    selectedImg = basketballShoesImgs.get(random.nextInt(basketballShoesImgs.size()));
                                                }

                                                ProductImage mainImage = ProductImage.builder()
                                                                .product(savedProduct)
                                                                .imageUrl(selectedImg)
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
                                                        randomSize = clothingSizes
                                                                        .get(random.nextInt(clothingSizes.size()));
                                                }

                                                String comboKey = randomColor.getId() + "-" + randomSize.getId();
                                                if (generatedCombos.contains(comboKey)) {
                                                        continue;
                                                }
                                                generatedCombos.add(comboKey);

                                                String sku = "SKU-" + savedProduct.getId() + "-" + randomColor.getId()
                                                                + "-"
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
                                users.add(User.builder().email("admin@brosport.com").passwordHash(defaultPassword)
                                                .fullName("Admin User").phone("0123456780").role("ADMIN").build());
                                users.add(User.builder().email("customer1@brosport.com").passwordHash(defaultPassword)
                                                .fullName("John Doe").phone("0123456781").role("USER").build());
                                users.add(User.builder().email("customer2@brosport.com").passwordHash(defaultPassword)
                                                .fullName("Jane Smith").phone("0123456782").role("USER").build());
                                users.add(User.builder().email("customer3@brosport.com").passwordHash(defaultPassword)
                                                .fullName("Mike Johnson").phone("0123456783").role("USER").build());
                                users.add(User.builder().email("customer4@brosport.com").passwordHash(defaultPassword)
                                                .fullName("Emily Davis").phone("0123456784").role("USER").build());
                                users = userRepository.saveAll(users);
                        } else {
                                users = userRepository.findAll();
                        }

                        // 9. Seed Cart, Order, Review
                        List<ProductDetail> allProductDetails = productDetailRepository.findAll();
                        if (!allProductDetails.isEmpty() && users.size() >= 5) {
                                User admin = users.stream().filter(u -> "ADMIN".equalsIgnoreCase(u.getRole()))
                                                .findFirst().orElse(users.get(0));
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
                                                        .totalPrice(BigDecimal.valueOf(100))
                                                        .shippingFee(BigDecimal.valueOf(5))
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
                                                        .price(productRepository.findById(
                                                                        allProductDetails.get(0).getProduct().getId())
                                                                        .get().getShowPrice())
                                                        .build();

                                        OrderItem oi2 = OrderItem.builder()
                                                        .order(order1)
                                                        .productDetail(allProductDetails.get(1))
                                                        .quantity(1)
                                                        .price(productRepository.findById(
                                                                        allProductDetails.get(1).getProduct().getId())
                                                                        .get().getShowPrice())
                                                        .build();

                                        order1.setOrderItems(Arrays.asList(oi1, oi2));
                                        orderRepository.save(order1);

                                        // Second order
                                        Order order2 = Order.builder()
                                                        .orderCode("BS-SEED-" + (System.currentTimeMillis() + 1))
                                                        .user(customer2)
                                                        .totalPrice(BigDecimal.valueOf(50))
                                                        .shippingFee(BigDecimal.valueOf(5))
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
                                                        .price(productRepository.findById(
                                                                        allProductDetails.get(2).getProduct().getId())
                                                                        .get().getShowPrice())
                                                        .build();

                                        order2.setOrderItems(Arrays.asList(oi3));
                                        orderRepository.save(order2);
                                }

                                // Reviews
                                if (reviewRepository.count() == 0) {
                                        log.info("Seeding Reviews...");
                                        java.util.Random random = new java.util.Random();
                                        List<Review> reviewsToSave = new ArrayList<>();

                                        // Get up to 5 products
                                        List<Product> productsToReview = productRepository.findAll().stream().limit(5)
                                                        .toList();

                                        String[] comments = {
                                                        "Great quality! Fits perfectly and very comfortable.",
                                                        "Good value for the price.",
                                                        "Excellent service, fast delivery.",
                                                        "Product is exactly as described.",
                                                        "Highly recommended, very durable material.",
                                                        "Will definitely order again.",
                                                        "Looks amazing in person.",
                                                        "Very satisfied with this purchase.",
                                                        "Size fits right, completely matches the guide.",
                                                        "It exceeded my expectations!"
                                        };

                                        for (Product p : productsToReview) {
                                                for (int i = 0; i < 10; i++) {
                                                        User randomUser = users.get(random.nextInt(users.size()));
                                                        int rating = random.nextInt(3) + 3; // 3 to 5 stars
                                                        Review review = Review.builder()
                                                                        .user(randomUser)
                                                                        .product(p)
                                                                        .rating(rating)
                                                                        .comment(comments[i % comments.length])
                                                                        .createdAt(LocalDateTime.now()
                                                                                        .minusDays(random.nextInt(30))
                                                                                        .minusHours(random.nextInt(24)))
                                                                        .build();
                                                        reviewsToSave.add(review);
                                                }
                                        }
                                        reviewRepository.saveAll(reviewsToSave);

                                        if (!reviewsToSave.isEmpty()) {
                                                List<Review> adminReplies = new ArrayList<>();
                                                String[] adminComments = {
                                                                "Thank you for trusting and supporting BroSport!",
                                                                "We’re glad you’re satisfied with the product, and we hope you’ll continue to support our shop.",
                                                                "Thank you for your review, we’ll strive to develop even more awesome designs!",
                                                                "BroSport sincerely thanks you!",
                                                                "Your feedback is a huge motivation for us!"
                                                };

                                                // Reply to ~30% of the reviews
                                                for (Review rev : reviewsToSave) {
                                                        if (random.nextInt(100) < 30) {
                                                                Review reply = Review.builder()
                                                                                .user(admin)
                                                                                .product(rev.getProduct())
                                                                                .rating(5)
                                                                                .comment(adminComments[random.nextInt(
                                                                                                adminComments.length)])
                                                                                .parentReview(rev)
                                                                                .createdAt(rev.getCreatedAt().plusHours(
                                                                                                random.nextInt(48) + 1)) // Reply
                                                                                                                         // after
                                                                                                                         // 1-48
                                                                                                                         // hours
                                                                                .build();
                                                                adminReplies.add(reply);
                                                        }
                                                }
                                                reviewRepository.saveAll(adminReplies);
                                        }
                                }

                                // 10. Seed Chatbox
                                if (chatConversationRepository.count() == 0) {
                                        log.info("Seeding Chatbox Data...");
                                        
                                        ChatConversation conv1 = ChatConversation.builder()
                                                .user(customer1)
                                                .userUnreadCount(1)
                                                .adminUnreadCount(0)
                                                .lastMessage("We've shipped your order!")
                                                .updatedAt(LocalDateTime.now())
                                                .build();
                                        chatConversationRepository.save(conv1);

                                        chatMessageRepository.save(ChatMessage.builder()
                                                .conversation(conv1)
                                                .senderEmail(customer1.getEmail())
                                                .content("Hello, do you have size 42 for the Nike running shoes?")
                                                .timestamp(LocalDateTime.now().minusHours(2))
                                                .isRead(true)
                                                .build());

                                        chatMessageRepository.save(ChatMessage.builder()
                                                .conversation(conv1)
                                                .senderEmail(admin.getEmail())
                                                .content("Yes we do! You can place an order directly.")
                                                .timestamp(LocalDateTime.now().minusHours(1))
                                                .isRead(true)
                                                .build());

                                        chatMessageRepository.save(ChatMessage.builder()
                                                .conversation(conv1)
                                                .senderEmail(admin.getEmail())
                                                .content("We've shipped your order!")
                                                .timestamp(LocalDateTime.now().minusMinutes(5))
                                                .isRead(false)
                                                .build());

                                        ChatConversation conv2 = ChatConversation.builder()
                                                .user(customer2)
                                                .userUnreadCount(0)
                                                .adminUnreadCount(1)
                                                .lastMessage("How can I return an item?")
                                                .updatedAt(LocalDateTime.now())
                                                .build();
                                        chatConversationRepository.save(conv2);

                                        chatMessageRepository.save(ChatMessage.builder()
                                                .conversation(conv2)
                                                .senderEmail(customer2.getEmail())
                                                .content("Hi Admin!")
                                                .timestamp(LocalDateTime.now().minusDays(1))
                                                .isRead(true)
                                                .build());

                                        chatMessageRepository.save(ChatMessage.builder()
                                                .conversation(conv2)
                                                .senderEmail(admin.getEmail())
                                                .content("Hello, how can I help you?")
                                                .timestamp(LocalDateTime.now().minusDays(1).plusMinutes(5))
                                                .isRead(true)
                                                .build());

                                        chatMessageRepository.save(ChatMessage.builder()
                                                .conversation(conv2)
                                                .senderEmail(customer2.getEmail())
                                                .content("How can I return an item?")
                                                .timestamp(LocalDateTime.now().minusMinutes(30))
                                                .isRead(false)
                                                .build());
                                }
                        }

                        log.info("Database seeding completed successfully! Entities inserted.");
                };
        }
}