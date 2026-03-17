package com.se2.demo.mapper;

import com.se2.demo.dto.response.OrderItemResponse;
import com.se2.demo.dto.response.OrderResponse;
import com.se2.demo.model.entity.Order;
import com.se2.demo.model.entity.OrderItem;
import com.se2.demo.model.entity.ProductDetail;
import com.se2.demo.model.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderNumber", source = "orderCode")
    @Mapping(target = "shipping", source = "shippingFee")
    @Mapping(target = "total", source = "totalPrice")
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "estimatedDelivery", expression = "java(java.time.LocalDate.now().plusDays(3).toString())")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(order.getOrderItems()))")
    OrderResponse toResponse(Order order);

    @Mapping(target = "productName", source = "productDetail.product.name")
    @Mapping(target = "price", source = "price")
    // Fix variantInfo: "Size: L | Color: Midnight Black"
    @Mapping(target = "variantInfo", expression = "java(\"Size: \" + entity.getProductDetail().getSize().getSizeDescription() + \" | Color: \" + entity.getProductDetail().getColor().getColorName())")
    @Mapping(target = "imageUrl", expression = "java(getMainImageUrl(entity.getProductDetail()))")
    OrderItemResponse toItemResponse(OrderItem entity);

    default BigDecimal calculateSubtotal(List<OrderItem> items) {
        if (items == null) return BigDecimal.ZERO;
        return items.stream()
                .map(i -> i.getPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default String getMainImageUrl(ProductDetail detail) {
        if (detail == null || detail.getVariantImages() == null || detail.getVariantImages().isEmpty()) return null;
        return detail.getVariantImages().stream()
                .filter(img -> img.getIsMain() != null && img.getIsMain())
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(detail.getVariantImages().get(0).getImageUrl());
    }
}