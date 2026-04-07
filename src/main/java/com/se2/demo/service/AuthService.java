package com.se2.demo.service;

import com.se2.demo.dto.request.RegisterRequest;
import com.se2.demo.dto.request.ResetPasswordRequest;
import jakarta.servlet.http.HttpSession;

public interface AuthService {
    void register(RegisterRequest request);

    void processForgotPassword(String email, HttpSession session);
    boolean verifyOtp(String otp, HttpSession session);
    void resetPassword(ResetPasswordRequest request, HttpSession session);
}
