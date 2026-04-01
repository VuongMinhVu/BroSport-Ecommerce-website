package com.se2.demo.service;

import com.se2.demo.dto.request.CartRequest;
import com.se2.demo.dto.response.CartResponse;
import java.util.List;

public interface CartService {
    List<CartResponse> getAllCarts();
    CartResponse getCartByUserId(Integer userId);
    CartResponse createCart(CartRequest request);
    CartResponse updateCart(Integer id, CartRequest request);
    void deleteCart(Integer id);
}