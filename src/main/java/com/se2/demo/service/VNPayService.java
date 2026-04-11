package com.se2.demo.service;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface VNPayService {
    // Tạo URL thanh toán
    String createPaymentUrl(BigDecimal amount, String orderInfo, String baseUrl);

    // Kiểm tra kết quả trả về từ VNPay
    int validatePayment(HttpServletRequest request);
}