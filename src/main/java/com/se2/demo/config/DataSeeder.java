//package com.se2.demo.config;
//
//import com.se2.demo.dto.async.ProductEvent;
//import com.se2.demo.mapper.ProductMapper;
//import com.se2.demo.model.entity.*;
//import com.se2.demo.repository.*;
//import com.se2.demo.utils.constant.Constant;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.math.BigDecimal;
//import java.util.*;
//
//@Configuration(proxyBeanMethods = false)
//@RequiredArgsConstructor
//@Slf4j
//public class DataSeeder {
//        private final ApplicationEventPublisher publisher;
//        private final ProductMapper productMapper;
//
//        @Bean
//        public CommandLineRunner initDatabase(
//                        CategoryRepository categoryRepository,
//                        BrandRepository brandRepository,
//                        ColorRepository colorRepository,
//                        SizeRepository sizeRepository,
//                        GenderRepository genderRepository,
//                        SportRepository sportRepository,
//                        ProductRepository productRepository,
//                        ProductDetailRepository productDetailRepository,
//                        ProductImageRepository productImageRepository) {
//
//                return args -> {
//                        log.info("Checking database for seed data...");
//
//                        if (productRepository.count() >= 100) {
//                                log.info("Database already seeded with enough products.");
//                                return;
//                        }
//
//                        log.info("Starting to seed database...");
//
//                        // 1. Seed Brands
//                        List<Brand> brands = new ArrayList<>();
//                        if (brandRepository.count() == 0) {
//                                brands = Arrays.asList(
//                                                Brand.builder().name("Nike")
//                                                                .logoUrl("https://picsum.photos/seed/nike/400/400")
//                                                                .description("Just do it").build(),
//                                                Brand.builder().name("Adidas")
//                                                                .logoUrl("https://picsum.photos/seed/adidas/400/400")
//                                                                .description("Impossible is nothing")
//                                                                .build(),
//                                                Brand.builder().name("Puma")
//                                                                .logoUrl("https://picsum.photos/seed/puma/400/400")
//                                                                .description("Forever Faster").build(),
//                                                Brand.builder().name("Under Armour")
//                                                                .logoUrl("https://picsum.photos/seed/ua/400/400")
//                                                                .description("Protect This House")
//                                                                .build(),
//                                                Brand.builder().name("New Balance")
//                                                                .logoUrl("https://picsum.photos/seed/nb/400/400")
//                                                                .description("Fearlessly Independent")
//                                                                .build());
//                                brandRepository.saveAll(brands);
//                        } else {
//                                brands = brandRepository.findAll();
//                        }
//
//                        // 2. Seed Categories
//                        List<Category> leafCategories = new ArrayList<>();
//                        if (categoryRepository.count() == 0) {
//                                Category shoes = Category.builder().name("Giày Thể Thao").slug("giay-the-thao")
//                                                .imageUrl("https://picsum.photos/seed/shoes/600/600")
//                                                .build();
//                                Category clothing = Category.builder().name("Quần Áo").slug("quan-ao")
//                                                .imageUrl("https://picsum.photos/seed/clothes/600/600")
//                                                .build();
//                                Category accessories = Category.builder().name("Phụ Kiện").slug("phu-kien")
//                                                .imageUrl("https://picsum.photos/seed/accessories/600/600")
//                                                .build();
//                                categoryRepository.saveAll(Arrays.asList(shoes, clothing, accessories));
//
//                                Category runningShoes = Category.builder().name("Giày Chạy Bộ").slug("giay-chay-bo")
//                                                .parentCategory(shoes).build();
//                                Category footballShoes = Category.builder().name("Giày Đá Bóng").slug("giay-da-bong")
//                                                .parentCategory(shoes).build();
//                                Category basketballShoes = Category.builder().name("Giày Bóng Rổ").slug("giay-bong-ro")
//                                                .parentCategory(shoes).build();
//
//                                Category tshirts = Category.builder().name("Áo Thun").slug("ao-thun")
//                                                .parentCategory(clothing).build();
//                                Category pants = Category.builder().name("Quần Dài").slug("quan-dai")
//                                                .parentCategory(clothing).build();
//                                Category shorts = Category.builder().name("Quần Short").slug("quan-short")
//                                                .parentCategory(clothing)
//                                                .build();
//
//                                categoryRepository
//                                                .saveAll(Arrays.asList(runningShoes, footballShoes, basketballShoes,
//                                                                tshirts, pants, shorts));
//                                leafCategories = Arrays.asList(runningShoes, footballShoes, basketballShoes, tshirts,
//                                                pants, shorts);
//                        } else {
//                                leafCategories = categoryRepository.findAll().stream()
//                                                .filter(c -> c.getParentCategory() != null)
//                                                .toList();
//                        }
//
//                        // 3. Seed Colors
//                        List<Color> colors = new ArrayList<>();
//                        if (colorRepository.count() == 0) {
//                                colors = Arrays.asList(
//                                                Color.builder().colorName("Đen").build(),
//                                                Color.builder().colorName("Trắng").build(),
//                                                Color.builder().colorName("Đỏ").build(),
//                                                Color.builder().colorName("Xanh Dương").build(),
//                                                Color.builder().colorName("Xám").build(),
//                                                Color.builder().colorName("Vàng").build());
//                                colorRepository.saveAll(colors);
//                        } else {
//                                colors = colorRepository.findAll();
//                        }
//
//                        // 4. Seed Sizes
//                        List<Size> sizes = new ArrayList<>();
//                        if (sizeRepository.count() == 0) {
//                                sizes = Arrays.asList(
//                                                Size.builder().sizeDescription("38").build(),
//                                                Size.builder().sizeDescription("39").build(),
//                                                Size.builder().sizeDescription("40").build(),
//                                                Size.builder().sizeDescription("41").build(),
//                                                Size.builder().sizeDescription("42").build(),
//                                                Size.builder().sizeDescription("43").build(),
//                                                Size.builder().sizeDescription("S").build(),
//                                                Size.builder().sizeDescription("M").build(),
//                                                Size.builder().sizeDescription("L").build(),
//                                                Size.builder().sizeDescription("XL").build());
//                                sizeRepository.saveAll(sizes);
//                        } else {
//                                sizes = sizeRepository.findAll();
//                        }
//
//                        // Phân loại size: giày (số), quần áo (chữ) an toàn hơn dùng subList
//                        List<Size> shoeSizes = sizes.stream()
//                                        .filter(s -> s.getSizeDescription().matches("\\d+"))
//                                        .toList();
//                        List<Size> clothingSizes = sizes.stream()
//                                        .filter(s -> !s.getSizeDescription().matches("\\d+"))
//                                        .toList();
//
//                        if (shoeSizes.isEmpty())
//                                shoeSizes = sizes;
//                        if (clothingSizes.isEmpty())
//                                clothingSizes = sizes;
//
//                        // 5. Seed Genders
//                        List<Gender> genders = new ArrayList<>();
//                        if (genderRepository.count() == 0) {
//                                genders = Arrays.asList(
//                                                Gender.builder().name("Nam").description("Sản phẩm dành cho nam")
//                                                                .logoUrl("https://picsum.photos/seed/male/400/400")
//                                                                .build(),
//                                                Gender.builder().name("Nữ").description("Sản phẩm dành cho nữ")
//                                                                .logoUrl("https://picsum.photos/seed/female/400/400")
//                                                                .build(),
//                                                Gender.builder().name("Trẻ em").description("Sản phẩm dành cho trẻ em")
//                                                                .logoUrl("https://picsum.photos/seed/kids/400/400")
//                                                                .build());
//                                genderRepository.saveAll(genders);
//                        } else {
//                                genders = genderRepository.findAll();
//                        }
//
//                        // 6. Seed Sports
//                        List<Sport> sports = new ArrayList<>();
//                        if (sportRepository.count() == 0) {
//                                sports = Arrays.asList(
//                                                Sport.builder().name("Bóng đá").description("Môn thể thao vua")
//                                                                .logoUrl("https://picsum.photos/seed/football/400/400")
//                                                                .build(),
//                                                Sport.builder().name("Chạy bộ").description("Rèn luyện sức khỏe")
//                                                                .logoUrl("https://picsum.photos/seed/running/400/400")
//                                                                .build(),
//                                                Sport.builder().name("Bóng rổ").description("Thể thao đồng đội")
//                                                                .logoUrl("https://picsum.photos/seed/basketball/400/400")
//                                                                .build(),
//                                                Sport.builder().name("Gym & Training").description("Tập luyện thể hình")
//                                                                .logoUrl("https://picsum.photos/seed/gym/400/400")
//                                                                .build());
//                                sportRepository.saveAll(sports);
//                        } else {
//                                sports = sportRepository.findAll();
//                        }
//
//                        // 6. Seed 100 Products
//                        log.info("Seeding 100 Products with variations...");
//                        Random random = new Random();
//
//                        for (int i = 1; i <= 100; i++) {
//                                Brand randomBrand = brands.get(random.nextInt(brands.size()));
//                                Category randomCategory = leafCategories.get(random.nextInt(leafCategories.size()));
//
//                                Gender randomGender = genders.get(random.nextInt(genders.size()));
//                                Sport randomSport = sports.get(random.nextInt(sports.size()));
//
//                                Map<String, Object> infoMap = new HashMap<>();
//                                infoMap.put("Môn thể thao", randomSport.getName());
//                                infoMap.put("Tính năng nổi bật", "Thấm hút mồ hôi, Mềm mại, Thoải mái");
//                                infoMap.put("Kiểu dáng", "Vừa vặn (Regular Fit)");
//                                infoMap.put("Thành phần", "100% Polyester cao cấp");
//
//                                String productName = randomCategory.getName() + " " + randomBrand.getName()
//                                                + " Phiên Bản " + i;
//                                String slug = "product-" + randomCategory.getSlug() + "-"
//                                                + randomBrand.getName().toLowerCase() + "-"
//                                                + i;
//
//                                BigDecimal price = BigDecimal.valueOf((random.nextInt(20) + 5) * 100000); // 500k to
//                                                                                                          // 2.5m
//                                BigDecimal comparePrice = price.multiply(BigDecimal.valueOf(1.2)); // 20% higher
//
//                                Product product = Product.builder()
//                                                .name(productName)
//                                                .slug(slug)
//                                                .description("Mô tả chi tiết cho " + productName
//                                                                + ". Một sản phẩm tuyệt vời mang lại hiệu suất thể thao tối đa. Chất liệu thoáng mát và co dãn tốt.")
//                                                .status("ACTIVE")
//                                                .feature("Ưu điểm nổi bật: Trọng lượng siêu nhẹ, công nghệ "
//                                                                + randomBrand.getName()
//                                                                + " độc quyền mang lại cảm giác dễ chịu suốt ngày.")
//                                                .information(infoMap)
//                                                .brand(randomBrand)
//                                                .category(randomCategory)
//                                                .gender(randomGender)
//                                                .sport(randomSport)
//                                                .originPrice(price)
//                                                .showPrice(comparePrice)
//                                                .build();
//
//                                Product savedProduct = productRepository.save(product);
//
//                                // Create ProductImages
//                                List<ProductImage> images = new ArrayList<>();
//                                int numImages = random.nextInt(3) + 1; // 1 to 3 images per product
//                                for (int imgIdx = 1; imgIdx <= numImages; imgIdx++) {
//                                        ProductImage mainImage = ProductImage.builder()
//                                                        .product(savedProduct)
//                                                        .imageUrl("https://picsum.photos/seed/"
//                                                                        + (savedProduct.getId() * 10 + imgIdx)
//                                                                        + "/800/800")
//                                                        .isMain(imgIdx == 1)
//                                                        .sortOrder(imgIdx)
//                                                        .build();
//                                        productImageRepository.save(mainImage);
//                                        images.add(mainImage);
//                                }
//                                savedProduct.setProductImages(images);
//
//                                // Create ProductDetails (Variants)
//                                List<ProductDetail> details = new ArrayList<>();
//                                int numVariants = random.nextInt(4) + 2; // 2 to 5 variants per product
//                                // Track generated combo to avoid duplicate SKU
//                                Set<String> generatedCombos = new HashSet<>();
//
//                                for (int v = 0; v < numVariants; v++) {
//                                        Color randomColor = colors.get(random.nextInt(colors.size()));
//                                        Size randomSize;
//                                        if (randomCategory.getName().toLowerCase().contains("giày")) {
//                                                randomSize = shoeSizes.get(random.nextInt(shoeSizes.size()));
//                                        } else {
//                                                randomSize = clothingSizes.get(random.nextInt(clothingSizes.size()));
//                                        }
//
//                                        String comboKey = randomColor.getId() + "-" + randomSize.getId();
//                                        if (generatedCombos.contains(comboKey)) {
//                                                continue; // Bỏ qua nếu đã bị trùng màu và size trong cùng sản phẩm
//                                        }
//                                        generatedCombos.add(comboKey);
//
//                                        String sku = "SKU-" + savedProduct.getId() + "-" + randomColor.getId() + "-"
//                                                        + randomSize.getId();
//
//                                        ProductDetail detail = ProductDetail.builder()
//                                                        .product(savedProduct)
//                                                        .color(randomColor)
//                                                        .size(randomSize)
//                                                        .stockQuantity(random.nextInt(100) + 10)
//                                                        .weightInGrams((float) (random.nextInt(500) + 200))
//                                                        .sku(sku)
//                                                        .build();
//
//                                        productDetailRepository.save(detail);
//                                        details.add(detail);
//                                }
//                                savedProduct.setProductDetails(details);
//
//                                // Publish event AFTER images + details are fully loaded in-memory
//                                this.publisher.publishEvent(
//                                                new ProductEvent(this.productMapper.toDocument(savedProduct),
//                                                                Constant.PRODUCT_CREATED_EVENT));
//                        }
//
//                        log.info(
//                                        "Database seeding completed successfully! 100 Products and their sub-variants have been generated.");
//                };
//        }
//}
