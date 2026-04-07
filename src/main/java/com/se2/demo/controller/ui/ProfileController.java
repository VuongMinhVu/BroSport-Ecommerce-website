package com.se2.demo.controller.ui;

import com.se2.demo.dto.request.ProfileUpdateRequest;
import com.se2.demo.model.entity.User;
import com.se2.demo.service.CloudinaryService;
import com.se2.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    @GetMapping("/profile/edit")
    public String showEditProfilePage(Model model, Principal principal) {
        String email = (principal != null) ? principal.getName() : "mockuser@example.com";
        User user;
        try {
            user = userService.getUserByEmail(email);
        } catch (Exception e) {
            user = User.builder()
                    .email(email)
                    .fullName("Mock User")
                    .phone("0123456789")
                    .address("123 Mock Street, City") // SỬA Ở ĐÂY: Dùng chuỗi fix cứng thay vì user.getAddress()
                    .avatarUrl("")
                    .build();
        }

        ProfileUpdateRequest form = ProfileUpdateRequest.builder()
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress()) // THÊM Ở ĐÂY: Truyền address vào form
                .avatarUrl(user.getAvatarUrl())
                .build();

        model.addAttribute("user", user);
        model.addAttribute("profileForm", form);

        return "account/profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(
            @Valid @ModelAttribute("profileForm") ProfileUpdateRequest form,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Model model,
            Principal principal) {

        String email = (principal != null) ? principal.getName() : "mockuser@example.com";
        User currentUser;
        try {
            currentUser = userService.getUserByEmail(email);
        } catch (Exception e) {
            currentUser = User.builder().email(email).fullName("Mock User").address("123 Mock Street").build();
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", currentUser);
            return "account/profile-edit";
        }

        // --- UPLOAD ẢNH QUA CLOUDINARY ---
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String uploadedUrl = cloudinaryService.uploadFile(avatarFile, "brosport/avatars");
                form.setAvatarUrl(uploadedUrl);
            } catch (Exception e) {
                model.addAttribute("user", currentUser);
                model.addAttribute("errorMessage", "Lỗi tải ảnh lên: " + e.getMessage());
                return "account/profile-edit";
            }
        } else {
            form.setAvatarUrl(currentUser.getAvatarUrl());
        }

        User updatedUser = currentUser;
        try {
            if (principal != null) {
                updatedUser = userService.updateProfile(email, form);
            } else {
                updatedUser.setFullName(form.getFullName());
                updatedUser.setPhone(form.getPhone());
                updatedUser.setAddress(form.getAddress()); // THÊM Ở ĐÂY: Update mock data
                updatedUser.setAvatarUrl(form.getAvatarUrl());
            }
        } catch (Exception e) {
            // Bỏ qua nếu là mock data
        }

        model.addAttribute("user", updatedUser);
        model.addAttribute("profileForm", ProfileUpdateRequest.builder()
                .fullName(updatedUser.getFullName())
                .phone(updatedUser.getPhone())
                .address(updatedUser.getAddress()) // THÊM Ở ĐÂY: Load lại address vào form sau khi update thành công
                .avatarUrl(updatedUser.getAvatarUrl())
                .build());
        model.addAttribute("successMessage", "Cập nhật hồ sơ thành công");

        return "account/profile-edit";
    }
}