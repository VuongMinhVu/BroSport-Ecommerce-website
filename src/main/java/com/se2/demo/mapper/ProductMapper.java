package com.se2.demo.mapper;

import com.se2.demo.dto.request.ProductRequest;
import com.se2.demo.dto.response.*;
import com.se2.demo.model.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", uses = { CommonMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "sport", ignore = true)
    @Mapping(target = "productDetails", ignore = true)
    @Mapping(target = "productImages", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(source = "category.name", target = "category")
    @Mapping(source = "brand.name", target = "brand")
    @Mapping(source = "gender.name", target = "gender")
    @Mapping(source = "sport.name", target = "sport")
    @Mapping(source = "category.sizeGuide", target = "sizeGuide")
    ProductResponse toResponse(Product entity);

    List<ProductResponse> toProductResponseList(List<Product> entities);

    @Mapping(source = "color.colorName", target = "color")
    @Mapping(source = "size.sizeDescription", target = "size")
    ProductDetailResponse toResponse(ProductDetail entity);

    List<ProductDetailResponse> toProductDetailResponseList(List<ProductDetail> entities);

    ProductImageResponse toResponse(ProductImage entity);

    List<ProductImageResponse> toProductImageResponseList(List<ProductImage> entities);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "slug", target = "slug")
    @Mapping(source = "originPrice", target = "showPrice")
    @Mapping(source = "showPrice", target = "originPrice")
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(source = "sport.name", target = "sportName")
    @Mapping(source = "gender.name", target = "genderName")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "thumbnail", expression = "java(getThumbnail(entity))")
    @Mapping(target = "availableColors", expression = "java(getAvailableColors(entity))")
    @Mapping(target = "availableSizes", expression = "java(getAvailableSizes(entity))")
    ProductDocument toDocument(Product entity);

    ProductDocumentResponse toDocumentResponse(ProductDocument entity);

    ProductNameResponse toProductNameResponse(ProductDocument entity);

    default String getThumbnail(Product product) {
        if (product.getProductImages() == null || product.getProductImages().isEmpty()) {
            return null;
        }
        return product.getProductImages().stream()
                .filter(img -> img.getImageUrl() != null && img.getIsMain())
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(product.getProductImages().get(0).getImageUrl());
    }

    default List<String> getAvailableColors(Product product) {
        if (product.getProductDetails() == null)
            return List.of();

        return product.getProductDetails().stream()
                .map(ProductDetail::getColor)
                .map(Color::getColorName)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    default List<String> getAvailableSizes(Product product) {
        if (product.getProductDetails() == null)
            return List.of();

        return product.getProductDetails().stream()
                .map(ProductDetail::getSize)
                .map(Size::getSizeDescription)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }
}
