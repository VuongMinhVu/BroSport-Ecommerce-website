package com.se2.demo.controller;

import com.se2.demo.dto.request.ProductFilterRequest;
import com.se2.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class WebController {

    private final ProductService productService;

    public WebController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping({"/products", "/products/{categorySlug}"})
    public String showProductList(
            @PathVariable(name = "categorySlug", required = false) String categorySlug,
            @ModelAttribute ProductFilterRequest filter,
            Model model) {
        
        if (categorySlug != null) {
            switch (categorySlug.toLowerCase()) {
                case "men":
                    filter.setGenderId(1); // MOCK: Thay ID thật của Men
                    break;
                case "women":
                    filter.setGenderId(2); // MOCK: Thay ID thật của Women
                    break;
                case "accessories":
                    filter.setCategoryId(3); // MOCK: Thay ID thật của Accessories
                    break;
                case "sports":
                    filter.setCategoryId(4); // MOCK: Thay ID thật của Sports
                    break;
                case "brands":
                    // filter logic for brands
                    break;
                case "sales":
                    // filter logic for sales
                    break;
            }
            model.addAttribute("categorySlug", categorySlug);
        }

        if (filter.getPage() == null || filter.getPage() < 0) {
            filter.setPage(0);
        }
        if (filter.getSize() == null || filter.getSize() <= 0) {
            filter.setSize(12); // Show 12 products per page
        }
        var products = productService.getAllProducts(filter);
        
        model.addAttribute("products", products.getContent());
        model.addAttribute("currentPage", products.getPageNo());
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalItems", products.getTotalElements());
        model.addAttribute("filter", filter);
        
        String title = categorySlug != null ? 
            categorySlug.substring(0, 1).toUpperCase() + categorySlug.substring(1) : 
            "Performance Gear";
        model.addAttribute("pageTitle", title);
        model.addAttribute("pageSubtitle", "Showing " + products.getTotalElements() + " results for " + title.toLowerCase());
        return "product-list";
    }

    @GetMapping("/cart")
    public String showCartDetail(Model model) {
        return "cart-detail";
    }

    public record MockUser(String name, String email, String avatarUrl) {}
    public record MockCartItem(String name, String size, String color, int quantity, double price, String imageUrl) {}

    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        // Mock User Data
        MockUser user = new MockUser(
            "John Doe", 
            "john.doe@example.com", 
            "https://lh3.googleusercontent.com/aida-public/AB6AXuBvfP7XDJdccDTVkPNoBeYDIZ1kZGgVxV_Iv57cDoT7P-XZf4KTfrVagCLjs8mRbU6fNzrmUS0LSTCivXTUOukWwOmZo2tn_rIkolQMMsTGs-nrgo4SL0GQBHmNHGWrgK70hqEzxNmpBJcM9Pzb2rR6ikzl7syfaUL8XCaznhz2XGLnS4TDN7XSoQbXmi9Q6ZJEaIHSTekLu1p6FCMyYthpX2xcDLB4ZFrcujPdKYR9BJiqRDRUgPrv12e-SQdHXGHkdlkz1GHvDXY"
        );
        model.addAttribute("user", user);

        // Mock Cart Items
        List<MockCartItem> cartItems = List.of(
            new MockCartItem("Pro Runner X1", "42", "Black/Red", 1, 120.00, "https://lh3.googleusercontent.com/aida-public/AB6AXuC1YAvHhIJi5tr_DsVeOlj7rpA5vaJMsO30cdRP2PK4TyXnyRF24Vxk5PX5fJokz_43zTxwt8aqGPP-EpdGsPL2IyTMao03fsWiTXWg-jTVZJqCNp-fdg4F48WXr9G_G-h6V-xCnRkoR9J2xU-VoL0b3C4DfwQsLnqyK2wGnReH_DbkCbc4716CFPE5SbLn0TH7rtmj7eSu6dwvrjGLgFUMAUnNzHQVGxQUOY4sGugkxxtXd6KbAa1g-kjuxbKykMlblPr12A7kWh0"),
            new MockCartItem("Elite Compression Tee", "L", "Yellow", 2, 90.00, "https://lh3.googleusercontent.com/aida-public/AB6AXuAgahF5CqJSzVit9W32QypZ0G9qoiFv3OjNzSRPz9OAfHXMXat8DPBhy0skyRC79hsOaxAI_RiY6-1gnYBxMxKUJ6HHGM1Tz_MX8Ug33xaw02suWfMMbiLoQUW6ccdaB07Fce4-ePpu6Hn32quJe0BrwZAjfRtOHPcp1gn6VJ8rTNnIZuks5YXn1xl1cgRtWRw4QdzBajKSyMVQp0C4HSURIGJUm3qaNKXtPcFHXu75uB9LAYYmI88tpqgyDDqgHEKq-_RLBQzzHmg")
        );
        model.addAttribute("cartItems", cartItems);

        // Calculate subtotal
        double subtotal = cartItems.stream().mapToDouble(item -> item.price() * item.quantity()).sum();
        model.addAttribute("subtotal", subtotal);

        return "checkout";
    }

    @PostMapping("/checkout/process")
    public String processCheckout() {
        return "redirect:/order-success";
    }

    @GetMapping("/homepage")
    public String Homepage(){
        return "homepage";
    }
    @GetMapping("/order-success")
    public String showOrderSuccess() {
        return "order-success";
    }
}
