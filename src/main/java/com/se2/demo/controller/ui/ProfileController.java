package com.se2.demo.controller.ui;

import com.se2.demo.dto.request.ChangePasswordRequest;
import com.se2.demo.dto.request.ProfileUpdateRequest;
import com.se2.demo.model.entity.User;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/account/profile/edit")
    public String showEditProfilePage(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        ProfileUpdateRequest form = ProfileUpdateRequest.builder()
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .build();

        model.addAttribute("user", user);
        model.addAttribute("profileForm", form);
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());

        return "account/profile-edit";
    }

    @PostMapping("/account/profile/edit")
    public String updateProfile(
            @Valid @ModelAttribute("profileForm") ProfileUpdateRequest form,
            BindingResult bindingResult,
            Model model,
            Principal principal
    ) {
        String email = principal.getName();
        User currentUser = userService.getUserByEmail(email);

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", currentUser);
            return "account/profile-edit";
        }

        User updatedUser = userService.updateProfile(email, form);

        model.addAttribute("user", updatedUser);
        model.addAttribute("profileForm", ProfileUpdateRequest.builder()
                .fullName(updatedUser.getFullName())
                .phone(updatedUser.getPhone())
                .avatarUrl(updatedUser.getAvatarUrl())
                .build());
        model.addAttribute("successMessage", "Cập nhật hồ sơ thành công");

        return "account/profile-edit";
    }

    @PostMapping("/profile/change-password")
    public String processChangePassword(
            @ModelAttribute ChangePasswordRequest request,
            Principal principal,
            RedirectAttributes redirectAttributes,
            HttpServletRequest httpServletRequest
    ) {
        try {
            String email = principal.getName();
            userService.changePassword(email, request);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        String referer = httpServletRequest.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/account/profile/edit");
    }
}
