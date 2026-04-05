package com.se2.demo.controller;

import com.se2.demo.dto.request.ProductFilterRequest;
import com.se2.demo.dto.response.ProductResponse;
import com.se2.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.se2.demo.service.CartService;
import com.se2.demo.service.OrderService;
import com.se2.demo.service.OrderService;
import com.se2.demo.dto.response.OrderDetailResponse;
import com.se2.demo.dto.response.CartResponse;
import com.se2.demo.dto.response.OrderDetailResponse;

import java.util.List;

@Controller
public class WebController {

    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;

    public WebController(ProductService productService, CartService cartService, OrderService orderService) {
        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping({ "/products", "/products/{categorySlug}" })
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

        // THÊM LOGIC ĐỔI TIÊU ĐỀ NẾU CÓ TỪ KHÓA TÌM KIẾM
        if (filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
            model.addAttribute("pageTitle", "Kết quả cho: '" + filter.getKeyword() + "'");
            model.addAttribute("pageSubtitle", "Tìm thấy " + products.getTotalElements() + " sản phẩm phù hợp");
        } else {
            model.addAttribute("pageTitle", "Performance Gear");
            model.addAttribute("pageSubtitle",
                    "Showing " + products.getTotalElements() + " results for high-performance athlete gear");
        }
        return "product-list";
    }

    @GetMapping("/cart")
    public String showCartDetail(Model model) {
        return "cart-detail";
    }

    public record MockUser(String name, String email, String avatarUrl) {
    }

    public record MockCartItem(String name, String size, String color, int quantity, double price, String imageUrl) {
    }

    @GetMapping("/checkout")
    public String showCheckout(
            @RequestParam(required = false) Boolean buyNow,
            @RequestParam(required = false) Integer variantId,
            @RequestParam(required = false) Integer qty,
            Model model) {

        // 1. Giả lập thông tin User (Giữ nguyên logic cũ của bạn)
        Integer currentUserId = 1;
        MockUser user = new MockUser(
                "John Doe",
                "john.doe@example.com",
                "https://lh3.googleusercontent.com/aida-public/AB6AXuBvfP7XDJdccDTVkPNoBeYDIZ1kZGgVxV_Iv57cDoT7P-XZf4KTfrVagCLjs8mRbU6fNzrmUS0LSTCivXTUOukWwOmZo2tn_rIkolQMMsTGs-nrgo4SL0GQBHmNHGWrgK70hqEzxNmpBJcM9Pzb2rR6ikzl7syfaUL8XCaznhz2XGLnS4TDN7XSoQbXmi9Q6ZJEaIHSTekLu1p6FCMyYthpX2xcDLB4ZFrcujPdKYR9BJiqRDRUgPrv12e-SQdHXGHkdlkz1GHvDXY");
        model.addAttribute("user", user);

        List<MockCartItem> displayItems = new java.util.ArrayList<>();
        double subtotal = 0.0;

        // 2. PHÂN LUỒNG XỬ LÝ DỮ LIỆU
        if (Boolean.TRUE.equals(buyNow) && variantId != null) {
            // LUỒNG MUA NGAY: Lấy thông tin sản phẩm trực tiếp từ DB
            try {
                // Sử dụng getProductById hoặc một hàm tương tự từ productService để lấy thông
                // tin variant
                ProductResponse product = productService.getProductByVariantId(variantId);

                // Lấy thông tin màu sắc/kích thước từ danh sách chi tiết của sản phẩm đó
                // Ở đây ta tìm đúng VariantId người dùng đã chọn
                var detail = product.getProductDetails().stream()
                        .filter(d -> d.getId().equals(variantId))
                        .findFirst()
                        .orElse(null);

                if (detail != null) {
                    MockCartItem item = new MockCartItem(
                            product.getName(),
                            detail.getSize(),
                            detail.getColor(),
                            qty != null ? qty : 1,
                            product.getShowPrice().doubleValue(),
                            product.getProductImages().isEmpty() ? ""
                                    : product.getProductImages().get(0).getImageUrl());
                    displayItems.add(item);
                    subtotal = item.price() * item.quantity();
                }
            } catch (Exception e) {
                // Nếu lỗi, điều hướng về giỏ hàng hoặc trang sản phẩm
                e.printStackTrace();
                return "redirect:/products";
            }
        } else {
            // LUỒNG GIỎ HÀNG: Logic cũ lấy từ CartService
            try {
                CartResponse cart = cartService.getCartByUserId(currentUserId);
                displayItems = cart.getCartDetails().stream()
                        .map(detail -> new MockCartItem(
                                detail.getProductName(),
                                detail.getSizeName(),
                                detail.getColorName(),
                                detail.getQuantity(),
                                detail.getUnitPrice(),
                                detail.getImageUrl()))
                        .toList();
                subtotal = displayItems.stream().mapToDouble(item -> item.price() * item.quantity()).sum();
            } catch (Exception e) {
                displayItems = List.of();
            }
        }

        // 3. Đẩy dữ liệu ra giao diện Checkout.html
        model.addAttribute("cartItems", displayItems);
        model.addAttribute("subtotal", subtotal);

        return "checkout";
    }

    @PostMapping("/checkout/process")
    public String processCheckout() {
        return "redirect:/order-success";
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
        return "pages/homepage";
    }

    @GetMapping("/order-success")
    public String showOrderSuccess(@RequestParam(required = false) String orderCode, Model model) {
        // In ra màn hình console của Spring Boot để kiểm tra
        System.out.println(">>> MÃ ĐƠN HÀNG TRÊN URL LÀ: " + orderCode);

        if (orderCode != null && !orderCode.isEmpty()) {
            try {
                OrderDetailResponse order = orderService.getOrderDetail(orderCode);
                model.addAttribute("order", order);
                System.out.println(">>> TÌM THẤY ĐƠN HÀNG: " + order.getOrderNumber());
            } catch (Exception e) {
                System.out.println(">>> LỖI KHI TÌM ĐƠN HÀNG TRONG DATABASE: " + e.getMessage());
                e.printStackTrace(); // In chi tiết lỗi ra console
            }
        } else {
            System.out.println(">>> LỖI: KHÔNG NHẬN ĐƯỢC MÃ ĐƠN HÀNG TỪ TRÌNH DUYỆT!");
        }
        return "order-success";
    }

    // Điều hướng đến trang Lịch sử đơn hàng
    @GetMapping("/order-history")
    public String orderHistoryPage() {
        return "orderHistory";
    }

    @GetMapping("/product/{slug}")
    public String showProductDetailPage(@PathVariable("slug") String slug, Model model) {
        // 1. Gọi service lấy dữ liệu sản phẩm dựa trên slug trên thanh địa chỉ
        ProductResponse product = productService.getProductBySlug(slug);

        // 2. Đưa đối tượng 'product' sang giao diện
        model.addAttribute("product", product);

        // 3. Trả về tên file HTML (productDetail.html)
        return "productDetail";
    }

    // Điều hướng đến trang Theo dõi đơn hàng
    @GetMapping("/order-tracking")
    public String orderTrackingPage() {
        return "orderTracking";
    }

    @GetMapping("/search-result")
    public String searchResultPage() {
        return "search-result"; // Trả về file search-result.html
    }
}
