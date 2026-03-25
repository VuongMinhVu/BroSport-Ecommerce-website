package com.se2.demo.controller.ui;

import com.se2.demo.dto.request.RegisterRequest;
import com.se2.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/login";
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
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }
}