package com.se2.demo.repository.specification;

import com.se2.demo.dto.request.ProductFilterRequest;
import com.se2.demo.model.entity.Product;
import com.se2.demo.model.entity.ProductDetail;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> filterProducts(ProductFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + filter.getKeyword().toLowerCase() + "%"));
            }

            if (filter.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), filter.getCategoryId()));
            }

            if (filter.getBrandId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), filter.getBrandId()));
            }

            if (filter.getGenderId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("gender").get("id"), filter.getGenderId()));
            }

            if (filter.getSportId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("sport").get("id"), filter.getSportId()));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("showPrice"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("showPrice"), filter.getMaxPrice()));
            }

            // JOIN with ProductDetail to filter by variations (color, size)
            if (filter.getColorId() != null || filter.getSizeId() != null) {

                // Use DISTINCT to avoid duplicate products if multiple variants match
                query.distinct(true);
                Join<Product, ProductDetail> detailJoin = root.join("productDetails", JoinType.INNER);

                if (filter.getColorId() != null) {
                    predicates.add(criteriaBuilder.equal(detailJoin.get("color").get("id"), filter.getColorId()));
                }

                if (filter.getSizeId() != null) {
                    predicates.add(criteriaBuilder.equal(detailJoin.get("size").get("id"), filter.getSizeId()));
                }
            }

            predicates.add(criteriaBuilder.equal(root.get("status"), "ACTIVE"));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
