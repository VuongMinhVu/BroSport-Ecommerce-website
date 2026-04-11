package com.se2.demo.controller.ui;

import com.se2.demo.dto.request.ChangePasswordRequest;
import com.se2.demo.dto.request.ProfileUpdateRequest;
import com.se2.demo.model.entity.User;
import com.se2.demo.service.CloudinaryService;
import com.se2.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    @GetMapping("/profile/edit")
    public String showEditProfilePage(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        ProfileUpdateRequest form = ProfileUpdateRequest.builder()
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .avatarUrl(user.getAvatarUrl())
                .build();

        model.addAttribute("profileForm", form);
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        model.addAttribute("user", user); // THÊM LẠI DÒNG NÀY

        return "account/profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(
            @Valid @ModelAttribute("profileForm") ProfileUpdateRequest form,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Model model,
            Principal principal) {

        String email = principal.getName();
        User currentUser = userService.getUserByEmail(email);

        if (bindingResult.hasErrors()) {
            return "account/profile-edit"; // Đã xóa model.addAttribute("user"...)
        }

        // --- UPLOAD ẢNH QUA CLOUDINARY ---
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String uploadedUrl = cloudinaryService.uploadFile(avatarFile, "brosport/avatars");
                form.setAvatarUrl(uploadedUrl);
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Lỗi tải ảnh lên: " + e.getMessage());
                return "account/profile-edit";
            }
        } else {
            form.setAvatarUrl(currentUser.getAvatarUrl());
        }

        // Update dữ liệu vào DB
        User updatedUser = userService.updateProfile(email, form);

        model.addAttribute("user", updatedUser);
        model.addAttribute("profileForm", ProfileUpdateRequest.builder()
                .fullName(updatedUser.getFullName())
                .phone(updatedUser.getPhone())
                .address(updatedUser.getAddress())
                .avatarUrl(updatedUser.getAvatarUrl())
                .build());
        model.addAttribute("successMessage", "Cập nhật hồ sơ thành công");

        return "account/profile-edit";
    }

    @GetMapping("/profile/change-password")
    public String showChangePasswordPage(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        model.addAttribute("user", user);

        return "account/change-password";
    }

    @PostMapping("/profile/change-password")
    public String processChangePassword(
            @Valid @ModelAttribute("changePasswordRequest") ChangePasswordRequest request,
            BindingResult bindingResult,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes,
            HttpServletRequest httpServletRequest) {
        try {
            String email = principal.getName();
            if (bindingResult.hasErrors()) {
                User user = userService.getUserByEmail(email);
                model.addAttribute("user", user);
                model.addAttribute("changePasswordRequest", request);
                return "account/change-password";
            }
            userService.changePassword(email, request);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công");
            return "redirect:/profile/change-password";
        } catch (RuntimeException e) {
            User user = userService.getUserByEmail(principal.getName());
            model.addAttribute("user", user);
            model.addAttribute("changePasswordRequest", request);
            model.addAttribute("errorMessage", e.getMessage());
            return "account/change-password";
        }
    }
}