package com.se2.demo.service.impl;

import com.se2.demo.dto.request.CartRequest;
import com.se2.demo.dto.response.CartResponse;
import com.se2.demo.model.entity.Cart;
import com.se2.demo.mapper.CartMapper;
import com.se2.demo.model.entity.User;
import com.se2.demo.repository.CartRepository;
import com.se2.demo.repository.UserRepository;
import com.se2.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final UserRepository userRepository;

    @Override
    public List<CartResponse> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        return cartMapper.toCartResponseList(carts);
    }

    @Override
    public CartResponse getCartByUserId(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for User ID: " + userId));
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse createCart(Integer userId) {

        Cart cart = new Cart();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        cart.setUser(user);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse updateCart(Integer id, Integer userId, CartRequest request) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        cart.setUser(user);

        return cartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void deleteCart(Integer id) {
        if (!cartRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Cart not found with id: " + id);
        }
        cartRepository.deleteById(id);
    }
}