package com.se2.demo.service.impl;

import com.se2.demo.dto.request.CartDetailRequest;
import com.se2.demo.dto.response.CartDetailResponse;
import com.se2.demo.model.entity.Cart;
import com.se2.demo.model.entity.CartDetail;
import com.se2.demo.mapper.CartMapper;
import com.se2.demo.model.entity.ProductDetail;
import com.se2.demo.repository.CartDetailRepository;
import com.se2.demo.repository.CartRepository;
import com.se2.demo.repository.ProductDetailRepository;
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
    private final ProductDetailRepository productDetailRepository;

    @Override
    @Transactional
    public CartDetailResponse addItemToCart(CartDetailRequest request) {
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại!"));

        Integer requestedQty = (request.getQuantity() == null || request.getQuantity() < 1) ? 1 : request.getQuantity();
        Optional<CartDetail> existingItem = cartDetailRepository
                .findByCartIdAndProductDetailId(request.getCartId(), request.getProductDetailId());

        CartDetail itemToSave;

        if (existingItem.isPresent()) {
            itemToSave = existingItem.get();
            itemToSave.setQuantity(requestedQty);
            itemToSave.setUpdatedAt(LocalDateTime.now());
        } else {
            ProductDetail productDetail = productDetailRepository.findById(request.getProductDetailId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

            itemToSave = new CartDetail();
            itemToSave.setCart(cart);
            itemToSave.setProductDetail(productDetail);
            itemToSave.setQuantity(requestedQty);
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
