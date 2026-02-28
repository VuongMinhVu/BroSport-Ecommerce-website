package com.se2.demo.config;

import com.se2.demo.model.entity.*;
import com.se2.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    @Bean
    public CommandLineRunner initDatabase(
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            ColorRepository colorRepository,
            SizeRepository sizeRepository,
            TargetCustomerRepository targetCustomerRepository,
            ProductRepository productRepository) {

        return args -> {
            log.info("Checking database for seed data...");

            // 1. Seed Brands
            if (brandRepository.count() == 0) {
                log.info("Seeding Brands");
                Brand nike = Brand.builder().name("Nike").logoUrl("nike.png").description("Just do it").build();
                Brand adidas = Brand.builder().name("Adidas").logoUrl("adidas.png").description("Impossible is nothing")
                        .build();
                brandRepository.saveAll(Arrays.asList(nike, adidas));
            }

            // 2. Seed Categories
            if (categoryRepository.count() == 0) {
                log.info("Seeding Categories");
                Category shoes = Category.builder().name("Giày Thể Thao").slug("giay-the-thao").imageUrl("shoes.jpg")
                        .build();
                Category clothing = Category.builder().name("Quần Áo").slug("quan-ao").imageUrl("clothes.jpg").build();
                categoryRepository.saveAll(Arrays.asList(shoes, clothing));

                Category runningShoes = Category.builder().name("Giày Chạy Bộ").slug("giay-chay-bo")
                        .parentCategory(shoes).build();
                categoryRepository.save(runningShoes);
            }

            // 3. Seed Colors
            if (colorRepository.count() == 0) {
                log.info("Seeding Colors");
                colorRepository.saveAll(Arrays.asList(
                        Color.builder().colorName("Đen").build(),
                        Color.builder().colorName("Trắng").build(),
                        Color.builder().colorName("Đỏ").build()));
            }

            // 4. Seed Sizes
            if (sizeRepository.count() == 0) {
                log.info("Seeding Sizes");
                sizeRepository.saveAll(Arrays.asList(
                        Size.builder().sizeDescription("40").build(),
                        Size.builder().sizeDescription("41").build(),
                        Size.builder().sizeDescription("42").build(),
                        Size.builder().sizeDescription("S").build(),
                        Size.builder().sizeDescription("M").build()));
            }

            // 5. Seed Target Customers
            if (targetCustomerRepository.count() == 0) {
                log.info("Seeding Target Customers");
                targetCustomerRepository.saveAll(Arrays.asList(
                        TargetCustomer.builder().name("Nam").build(),
                        TargetCustomer.builder().name("Nữ").build(),
                        TargetCustomer.builder().name("Trẻ em").build()));
            }

            log.info("Database seeding completed!");
        };
    }
}
