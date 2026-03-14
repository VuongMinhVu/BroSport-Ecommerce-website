package com.se2.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {

    @GetMapping("/products")
    public String showProductList(Model model) {
        // You can add attributes to the model here if needed
        return "product-list";
    }

    @GetMapping("/cart")
    public String showCartDetail(Model model) {
        return "cart-detail";
    }

    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        return "checkout";
    }

    @PostMapping("/checkout/process")
    public String processCheckout() {
        return "redirect:/order-success";
    }

    @GetMapping("/order-success")
    public String showOrderSuccess() {
        return "order-success";
    }
}
