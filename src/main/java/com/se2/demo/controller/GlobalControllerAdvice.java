package com.se2.demo.controller;

import com.se2.demo.model.entity.User;
import com.se2.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

  private final UserService userService;

  // Phương thức này sẽ tự động thêm đối tượng 'user' vào Model của TẤT CẢ các
  // request
  @ModelAttribute
  public void addUserToModel(Model model, Principal principal) {
    if (principal != null) {
      String email = principal.getName();
      try {
        User user = userService.getUserByEmail(email);
        // Biến 'user' này sẽ dùng được ở Header, Sidebar và mọi trang .html
        model.addAttribute("user", user);
      } catch (Exception e) {
        // Xử lý nếu không tìm thấy user
      }
    }
  }
}