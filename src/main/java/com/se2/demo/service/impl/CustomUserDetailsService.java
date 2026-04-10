package com.se2.demo.service.impl;

import com.se2.demo.model.entity.User;
import com.se2.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Chặn lỗi 500 nếu dữ liệu từ form gửi lên bị null
        if (email == null || email.trim().isEmpty()) {
            throw new UsernameNotFoundException("Email không được để trống");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + email));

        String role = user.getRole() == null || user.getRole().isBlank() ? "USER" : user.getRole();

        // Xóa chữ "ROLE_" nếu trong Database lỡ lưu thừa, vì hàm .roles() sẽ tự động
        // thêm vào
        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash()) // Mật khẩu đã được hash bằng BCrypt
                .roles(role) // Dùng .roles() an toàn và chuẩn cấu trúc của Spring Security hơn
                .build();
    }
}