package com.se2.demo.service;

import com.se2.demo.model.entity.Order;

public interface EmailService {
    void sendOrderSuccessEmail(Integer orderId);

    void sendOtpEmail(String email, String otp);
}