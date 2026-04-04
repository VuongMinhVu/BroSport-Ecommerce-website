package com.se2.demo.controller;

import com.se2.demo.dto.request.CartRequest;
import com.se2.demo.dto.response.CartResponse;
import com.se2.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 1. Read all
    @GetMapping
    public ResponseEntity<List<CartResponse>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    // 2. Read One
    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    // 3. Tạo mới một giỏ hàng (Create)
    @PostMapping
    public ResponseEntity<CartResponse> createCart(@RequestBody CartRequest request) {
        return new ResponseEntity<>(cartService.createCart(request), HttpStatus.CREATED);
    }

    // 4. Cập nhật thông tin giỏ hàng (Update)
    @PutMapping("/{id}")
    public ResponseEntity<CartResponse> updateCart(@PathVariable Integer id, @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.updateCart(id, request));
    }

    // 5. Xóa giỏ hàng (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Integer id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }
}