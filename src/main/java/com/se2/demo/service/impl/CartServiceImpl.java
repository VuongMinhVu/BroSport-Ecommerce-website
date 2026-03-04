package com.se2.demo.service.impl;

import com.se2.demo.dto.request.CartRequest;
import com.se2.demo.dto.response.CartResponse;
import com.se2.demo.model.entity.Cart;
import com.se2.demo.repository.CartRepository;
import com.se2.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    public List<CartResponse> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CartResponse getCartById(Integer id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + id));
        return mapToResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse createCart(CartRequest request) {
        Cart cart = new Cart();
        cart.setUserId(request.getUserId());
        cart.setProductDetailId(request.getProductDetailId());
        cart.setUpdatedAt(LocalDateTime.now());

        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse updateCart(Integer id, CartRequest request) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + id));

        cart.setUserId(request.getUserId());
        cart.setProductDetailId(request.getProductDetailId());
        cart.setUpdatedAt(LocalDateTime.now());

        Cart updatedCart = cartRepository.save(cart);
        return mapToResponse(updatedCart);
    }

    @Override
    @Transactional
    public void deleteCart(Integer id) {
        if (!cartRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Cart not found with id: " + id);
        }
        cartRepository.deleteById(id);
    }

    // Helper method để convert Entity sang Response DTO
    private CartResponse mapToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setProductDetailId(cart.getProductDetailId());
        response.setUpdatedAt(cart.getUpdatedAt());
        return response;
    }
}