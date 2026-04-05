package com.se2.demo.service.impl;

import com.se2.demo.dto.request.RegisterRequest;
import com.se2.demo.dto.request.ResetPasswordRequest;
import com.se2.demo.model.entity.User;
import com.se2.demo.repository.UserRepository;
import com.se2.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        User user = User.builder()
                .email(email)
                .fullName(request.getFullName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(null)
                .role("USER")
                .avatarUrl(null)
                .build();

        userRepository.save(user);
    }

    @Override
    public void processForgotPassword(String email, HttpSession session) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống."));

        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            throw new RuntimeException("Tài khoản chưa liên kết số điện thoại.");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));

        // Lưu OTP và email vào Session
        session.setAttribute("RESET_EMAIL", email);
        session.setAttribute("RESET_OTP", otp);

        // GIẢ LẬP GỬI SMS: Sau này thay bằng code gọi API Twilio/SpeedSMS
        log.info("[MOCK SMS] Đã gửi mã OTP: {} đến số điện thoại: {}", otp, user.getPhone());
    }

    @Override
    public boolean verifyOtp(String otp, HttpSession session) {
        String sessionOtp = (String) session.getAttribute("RESET_OTP");
        return sessionOtp != null && sessionOtp.equals(otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request, HttpSession session) {
        String email = (String) session.getAttribute("RESET_EMAIL");
        if (email == null) throw new RuntimeException("Phiên làm việc đã hết hạn. Vui lòng thử lại.");

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp.");
        }

        User user = userRepository.findByEmail(email).orElseThrow();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        session.removeAttribute("RESET_EMAIL");
        session.removeAttribute("RESET_OTP");
    }
}
