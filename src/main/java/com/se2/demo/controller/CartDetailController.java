package com.se2.demo.controller;

import com.se2.demo.dto.request.CartDetailRequest;
import com.se2.demo.dto.response.CartDetailResponse;
import com.se2.demo.model.entity.User;
import com.se2.demo.repository.CartRepository;
import com.se2.demo.service.CartDetailService;
import com.se2.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/cart-details")
@RequiredArgsConstructor
public class CartDetailController {

    private final CartDetailService cartDetailService;
    private final UserService userService;

    // 1. (Create/Update logic)
    @PostMapping
    public ResponseEntity<CartDetailResponse> addItemToCart(
            @RequestBody CartDetailRequest request,
            Principal principal
    ) {
        User user = userService.getUserByEmail(principal.getName());
        CartDetailResponse response = cartDetailService.addItemToCart(user.getId(), request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Integer id) {
        cartDetailService.removeItemFromCart(id);
        return ResponseEntity.noContent().build();
    }
}