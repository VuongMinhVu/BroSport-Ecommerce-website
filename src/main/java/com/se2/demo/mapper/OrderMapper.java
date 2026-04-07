package com.se2.demo.mapper;

import com.se2.demo.dto.response.OrderDetailResponse;
import com.se2.demo.dto.response.OrderHistoryResponse;
import com.se2.demo.dto.response.OrderItemResponse;
import com.se2.demo.dto.response.OrderResponse;
import com.se2.demo.model.entity.Order;
import com.se2.demo.model.entity.OrderItem;
import com.se2.demo.model.entity.ProductDetail;
import com.se2.demo.model.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
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
    @Mapping(target = "variantInfo", expression = "java(\"Size: \" + entity.getProductDetail().getSize().getSizeDescription() + \" | Color: \" + entity.getProductDetail().getColor().getColorName())")
    @Mapping(target = "imageUrl", expression = "java(getMainImageUrl(entity.getProductDetail()))")
    OrderItemResponse toItemResponse(OrderItem entity);

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "orderNumber", source = "orderCode")
    @Mapping(target = "total", source = "totalPrice")
    @Mapping(target = "status", source = "orderStatus")
    @Mapping(target = "itemCount", expression = "java(order.getOrderItems() != null ? order.getOrderItems().size() : 0)")
    @Mapping(target = "date", expression = "java(order.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern(\"MMM dd, yyyy\", java.util.Locale.ENGLISH)))")
    OrderHistoryResponse toHistoryResponse(Order order);

    List<OrderHistoryResponse> toHistoryResponseList(List<Order> orders);

    @Mapping(target = "orderNumber", source = "orderCode")
    @Mapping(target = "createdAt", expression = "java(order.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern(\"MMMM dd, yyyy\", java.util.Locale.ENGLISH)))")
    @Mapping(target = "recipientName", source = "fullName")
    @Mapping(target = "shippingAddress", source = "shippingAddressFull")
    @Mapping(target = "shippingMethod", constant = "Express Courier (2-3 Days)")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(order.getOrderItems()))")
    @Mapping(target = "shippingFee", source = "shippingFee")
    @Mapping(target = "tax", constant = "0")
    @Mapping(target = "discount", expression = "java(order.getDiscountPrice() != null ? order.getDiscountPrice() : java.math.BigDecimal.ZERO)")
    @Mapping(target = "items", source = "orderItems")
    OrderDetailResponse toDetailResponse(Order order);

    default BigDecimal calculateSubtotal(List<OrderItem> items) {
        if (items == null) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default String getMainImageUrl(ProductDetail detail) {
        if (detail == null) {
            return null;
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
        return null;
    }
}
