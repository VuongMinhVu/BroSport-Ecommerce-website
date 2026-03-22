package com.se2.demo.repository;

import com.se2.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Tìm kiếm người dùng bằng Email (Thường dùng cho Login hoặc gửi Mail)
    Optional<User> findByEmail(String email);

    // Kiểm tra xem Email đã tồn tại chưa khi đăng ký
    boolean existsByEmail(String email);
}