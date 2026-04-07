package com.se2.demo.controller;

import com.se2.demo.model.entity.User;
import com.se2.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

// Báo cho Spring Boot biết: Hãy áp dụng class này cho toàn bộ các Controller trả về giao diện (HTML)
@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
public class GlobalControllerAdvice {

  private final UserService userService;

  // Hàm này sẽ TỰ ĐỘNG chạy trước mọi request để nạp biến "user" vào Model
  @ModelAttribute("user")
  public User globalUser(Principal principal) {
    if (principal != null) {
      try {
        // Lấy thông tin user thật từ DB dựa vào email đang đăng nhập
        return userService.getUserByEmail(principal.getName());
      } catch (Exception e) {
        return null;
      }
    }
    // Nếu chưa đăng nhập, trả về null để Header hiện giao diện khách (icon mặc
    // định)
    return null;
  }
}