package com.se2.demo.controller;

import com.se2.demo.dto.request.OrderRequest;
import com.se2.demo.dto.response.OrderDetailResponse;
import com.se2.demo.dto.response.OrderHistoryResponse;
import com.se2.demo.dto.response.OrderResponse;
import com.se2.demo.model.entity.User;
import com.se2.demo.service.OrderService;
import com.se2.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @RequestBody OrderRequest request,
            HttpServletRequest httpServletRequest,
            Principal principal
    ) {
        User user = userService.getUserByEmail(principal.getName());
        OrderResponse response = orderService.checkout(user, request, httpServletRequest);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/vnpay-callback")
    public ResponseEntity<Void> handleCallback(HttpServletRequest request) {
        try {
            OrderResponse response = orderService.processPaymentCallback(request);
            String vnp_TxnRef = request.getParameter("vnp_TxnRef");
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/order-success?orderCode=" + vnp_TxnRef))
                    .build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/cart?error=true"))
                    .build();
        }
    }

    // xem lich su
    @GetMapping("/my-history")
    public ResponseEntity<List<OrderHistoryResponse>> getMyOrderHistory(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        List<OrderHistoryResponse> history = orderService.getOrderHistory(user.getId());
        return ResponseEntity.ok(history);
    }
    @GetMapping("/{orderCode}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable String orderCode) {
        OrderDetailResponse detail = orderService.getOrderDetail(orderCode);
        return ResponseEntity.ok(detail);
    }
}