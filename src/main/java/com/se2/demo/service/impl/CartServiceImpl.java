package com.se2.demo.service.impl;

import com.se2.demo.dto.request.CartRequest;
import com.se2.demo.dto.response.CartResponse;
import com.se2.demo.model.entity.Cart;
import com.se2.demo.model.entity.Product;
import com.se2.demo.model.entity.ProductDetail;
import com.se2.demo.repository.CartRepository;
import com.se2.demo.repository.ProductDetailRepository;
import com.se2.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductDetailRepository productDetailRepository;

    @Override
    public List<CartResponse> getAllCarts() {
        List<Cart> allCarts = cartRepository.findAll();
        Map<Integer, List<Cart>> grouped = allCarts.stream().collect(Collectors.groupingBy(Cart::getProductDetailId));
        return grouped.values().stream().map(carts -> {
            Cart firstCart = carts.get(0);
            CartResponse res = mapToResponse(firstCart);
            res.setQuantity(carts.size());
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public CartResponse getCartById(Integer id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + id));
        CartResponse res = mapToResponse(cart);
        res.setQuantity(1);
        return res;
    }

    @Override
    @Transactional
    public CartResponse createCart(CartRequest request) {
        Cart cart = new Cart();
        cart.setUserId(request.getUserId());
        cart.setProductDetailId(request.getProductDetailId());
        cart.setUpdatedAt(LocalDateTime.now());

        Cart savedCart = cartRepository.save(cart);
        CartResponse res = mapToResponse(savedCart);
        res.setQuantity(1);
        return res;
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
        CartResponse res = mapToResponse(updatedCart);
        res.setQuantity(1);
        return res;
    }

    @Override
    @Transactional
    public void deleteCart(Integer id) {
        if (!cartRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Cart not found with id: " + id);
        }
        cartRepository.deleteById(id);
    }

    private CartResponse mapToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setProductDetailId(cart.getProductDetailId());
        response.setUpdatedAt(cart.getUpdatedAt());

        Optional<ProductDetail> detailOpt = productDetailRepository.findById(cart.getProductDetailId());
        if (detailOpt.isPresent()) {
            ProductDetail detail = detailOpt.get();
            Product product = detail.getProduct();
            if (product != null) {
                response.setProductName(product.getName());
                response.setProductPrice(product.getShowPrice() != null ? product.getShowPrice().doubleValue() : 0.0);
                if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                    response.setProductImage(product.getProductImages().get(0).getImageUrl());
                }
            }
            if (detail.getColor() != null) {
                response.setColor(detail.getColor().getColorName());
            }
            if (detail.getSize() != null) {
                response.setSize(detail.getSize().getSizeDescription());
            }
        }
        return response;
    }
}