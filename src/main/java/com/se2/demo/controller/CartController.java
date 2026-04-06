package com.se2.demo.controller;

import com.se2.demo.dto.request.CartRequest;
import com.se2.demo.dto.response.CartResponse;
import com.se2.demo.model.entity.User;
import com.se2.demo.service.CartService;
import com.se2.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    // 1. Read all
    @GetMapping
    public ResponseEntity<List<CartResponse>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    // 2. Read my cart
    @GetMapping("/my-cart")
    public ResponseEntity<CartResponse> getMyCart(Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        return ResponseEntity.ok(cartService.getCartByUserId(user.getId()));
    }
    // 3. Tạo mới một giỏ hàng (Create)
    @PostMapping
    public ResponseEntity<CartResponse> createCart(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return new ResponseEntity<>(cartService.createCart(user.getId()), HttpStatus.CREATED);
    }
    // 4. Cập nhật thông tin giỏ hàng (Update)
    @PutMapping("/{id}")
    public ResponseEntity<CartResponse> updateCart(@PathVariable Integer id, @RequestBody CartRequest request,  Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(cartService.updateCart(id, user.getId(), request));
    }

    // 5. Xóa giỏ hàng (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Integer id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }
}