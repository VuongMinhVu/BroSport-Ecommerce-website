package com.se2.demo.service;

public interface EmailService {

    void sendOrderSuccessEmail(Integer orderId);

    void sendOtpEmail(String email, String otp);
}