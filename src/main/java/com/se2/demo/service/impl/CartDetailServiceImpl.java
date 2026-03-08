package com.se2.demo.service.impl;

import com.se2.demo.dto.request.CartDetailRequest;
import com.se2.demo.dto.response.CartDetailResponse;
import com.se2.demo.model.entity.Cart;
import com.se2.demo.model.entity.CartDetail;
import com.se2.demo.mapper.CartMapper;
import com.se2.demo.repository.CartDetailRepository;
import com.se2.demo.repository.CartRepository;
import com.se2.demo.service.CartDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartDetailServiceImpl implements CartDetailService {

    private final CartDetailRepository cartDetailRepository;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartDetailResponse addItemToCart(CartDetailRequest request) {
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại!"));

        Optional<CartDetail> existingItem = cartDetailRepository
                .findByCartIdAndProductDetailId(request.getCartId(), request.getProductDetailId());

        CartDetail itemToSave;

        if (existingItem.isPresent()) {
            // Logic UPDATE
            itemToSave = existingItem.get();
            itemToSave.setUpdatedAt(LocalDateTime.now());
        } else {
            // Logic CREATE
            itemToSave = cartMapper.toEntity(request);
            itemToSave.setCart(cart);
            itemToSave.setAddedAt(LocalDateTime.now());
        }

        CartDetail savedItem = cartDetailRepository.save(itemToSave);
        return cartMapper.toResponse(savedItem);
    }

    @Override
    @Transactional
    public void removeItemFromCart(Integer detailId) {
        // Logic DELETE
        if (!cartDetailRepository.existsById(detailId)) {
            throw new RuntimeException("Không tìm thấy sản phẩm để xóa!");
        }
        cartDetailRepository.deleteById(detailId);
    }
}