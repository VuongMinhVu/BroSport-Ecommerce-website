package com.se2.demo.mapper;

import java.time.LocalDateTime;
import com.se2.demo.dto.request.CartDetailRequest;
import com.se2.demo.dto.request.CartRequest;
import com.se2.demo.dto.response.CartResponse;
import com.se2.demo.dto.response.CartDetailResponse;
import com.se2.demo.model.entity.Cart;
import com.se2.demo.model.entity.CartDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {LocalDateTime.class})
public interface CartMapper {

    // Mapping cho Cart
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cartDetails", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    Cart toEntity(CartRequest request);

    CartResponse toResponse(Cart entity);

    List<CartResponse> toCartResponseList(List<Cart> entities);

    // Mapping cho CartDetail
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart.id", source = "cartId")
    @Mapping(target = "addedAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    CartDetail toEntity(CartDetailRequest request);

    @Mapping(target = "cartId", source = "cart.id")
    CartDetailResponse toResponse(CartDetail entity);

    List<CartDetailResponse> toCartDetailResponseList(List<CartDetail> entities);
}