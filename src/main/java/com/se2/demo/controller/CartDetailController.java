package com.se2.demo.controller;

import com.se2.demo.dto.request.CartDetailRequest;
import com.se2.demo.dto.response.CartDetailResponse;
import com.se2.demo.service.CartDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart-details")
@RequiredArgsConstructor
public class CartDetailController {

    private final CartDetailService cartDetailService;

    // 1. (Create/Update logic)
    @PostMapping
    public ResponseEntity<CartDetailResponse> addItemToCart(@RequestBody CartDetailRequest request) {
        return new ResponseEntity<>(cartDetailService.addItemToCart(request), HttpStatus.CREATED);
    }

    // 2. (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Integer id) {
        cartDetailService.removeItemFromCart(id);
        return ResponseEntity.noContent().build();
    }
}