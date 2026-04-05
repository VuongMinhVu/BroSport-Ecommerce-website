package com.se2.demo.controller.ui;

import com.se2.demo.dto.request.*;
import com.se2.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        return "auth/forgot-password";
    }

    @GetMapping("/verify-otp")
    public String verifyOtp(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/verify-otp";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue(
                    "confirmPassword",
                    "error.confirmPassword",
                    "Mật khẩu nhập lại không khớp"
            );
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(request);
            return "redirect:/login?success";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, HttpSession session, RedirectAttributes redirect) {
        try {
            authService.processForgotPassword(email, session);
            return "redirect:/verify-otp";
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/forgot-password";
        }
    }

    @PostMapping("/verify-otp")
    public String handleVerifyOtp(@ModelAttribute VerifyOtpRequest request, HttpSession session, RedirectAttributes redirect) {
        if (authService.verifyOtp(request.getOtpCode(), session)) {
            return "redirect:/reset-password";
        }
        redirect.addFlashAttribute("errorMessage", "Mã OTP không chính xác hoặc đã hết hạn.");
        return "redirect:/verify-otp";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@ModelAttribute ResetPasswordRequest request, HttpSession session, RedirectAttributes redirect) {
        try {
            authService.resetPassword(request, session);
            return "redirect:/login?resetSuccess=true";
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reset-password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(Model model) {
        model.addAttribute("resetPasswordRequest", new ResetPasswordRequest());
        return "auth/reset-password";
    }
}