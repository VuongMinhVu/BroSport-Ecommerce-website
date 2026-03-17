package com.se2.demo.service;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface VNPayService {
    // Tạo URL thanh toán để gửi cho Frontend
    String createPaymentUrl(BigDecimal amount, String orderInfo, String baseUrl);

    // Kiểm tra kết quả trả về từ VNPay (0: Thất bại, 1: Thành công, -1: Sai chữ ký)
    int validatePayment(HttpServletRequest request);
}