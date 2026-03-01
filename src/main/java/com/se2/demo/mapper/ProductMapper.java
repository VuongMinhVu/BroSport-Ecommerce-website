package com.se2.demo.mapper;

import com.se2.demo.dto.request.ProductRequest;
import com.se2.demo.dto.response.ProductDetailResponse;
import com.se2.demo.dto.response.ProductImageResponse;
import com.se2.demo.dto.response.ProductResponse;
import com.se2.demo.model.entity.Product;
import com.se2.demo.model.entity.ProductDetail;
import com.se2.demo.model.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

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
    ProductResponse toResponse(Product entity);

    List<ProductResponse> toProductResponseList(List<Product> entities);

    @Mapping(source = "color.colorName", target = "color")
    @Mapping(source = "size.sizeDescription", target = "size")
    ProductDetailResponse toResponse(ProductDetail entity);

    List<ProductDetailResponse> toProductDetailResponseList(List<ProductDetail> entities);

    ProductImageResponse toResponse(ProductImage entity);

    List<ProductImageResponse> toProductImageResponseList(List<ProductImage> entities);
}
