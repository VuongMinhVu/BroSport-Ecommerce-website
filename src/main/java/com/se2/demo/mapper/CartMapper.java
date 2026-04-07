package com.se2.demo.mapper;

import java.time.LocalDateTime;

import com.se2.demo.dto.request.CartRequest;
import com.se2.demo.dto.response.CartResponse;
import com.se2.demo.dto.response.CartDetailResponse;
import com.se2.demo.model.entity.Cart;
import com.se2.demo.model.entity.CartDetail;
import com.se2.demo.model.entity.ProductDetail;
import com.se2.demo.model.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = { LocalDateTime.class })
public interface CartMapper {

    Cart toEntity(CartRequest request);

    List<CartResponse> toCartResponseList(List<Cart> carts);

    @Mapping(target = "cartDetails", source = "cartDetails")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(entity.getCartDetails()))")
    @Mapping(target = "shipping", constant = "0.0")
    @Mapping(target = "tax", expression = "java(calculateSubtotal(entity.getCartDetails()) * 0.08)")
    @Mapping(target = "total", expression = "java(calculateSubtotal(entity.getCartDetails()) * 1.08)")
    CartResponse toResponse(Cart entity);

    @Mapping(target = "cartId", source = "cart.id")
    @Mapping(target = "productDetailId", source = "productDetail.id")
    @Mapping(target = "productName", source = "productDetail.product.name")
    @Mapping(target = "unitPrice", source = "productDetail.product.showPrice")
    @Mapping(target = "colorName", source = "productDetail.color.colorName")
    @Mapping(target = "sizeName", source = "productDetail.size.sizeDescription")
    @Mapping(target = "totalPrice", expression = "java(entity.getProductDetail().getProduct().getShowPrice().doubleValue() * entity.getQuantity())")
    // Logic tìm ảnh isMain hoặc ảnh đầu tiên
    @Mapping(target = "imageUrl", expression = "java(getMainImageUrl(entity.getProductDetail()))")
    CartDetailResponse toResponse(CartDetail entity);

    default String getMainImageUrl(ProductDetail detail) {
        if (detail == null) {
            return "default_image_url.png";
        }

        if (detail.getVariantImages() != null && !detail.getVariantImages().isEmpty()) {
            return detail.getVariantImages().stream()
                    .filter(img -> img.getIsMain() != null && img.getIsMain())
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(detail.getVariantImages().get(0).getImageUrl());
        }

        if (detail.getProduct() != null
                && detail.getProduct().getProductImages() != null
                && !detail.getProduct().getProductImages().isEmpty()) {
            return detail.getProduct().getProductImages().stream()
                    .filter(img -> img.getIsMain() != null && img.getIsMain())
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(detail.getProduct().getProductImages().get(0).getImageUrl());
        }

        return "default_image_url.png";
    }

    default Double calculateSubtotal(List<CartDetail> details) {
        if (details == null)
            return 0.0;
        return details.stream()
                .mapToDouble(d -> d.getProductDetail().getProduct().getShowPrice().doubleValue() * d.getQuantity())
                .sum();
    }
}
