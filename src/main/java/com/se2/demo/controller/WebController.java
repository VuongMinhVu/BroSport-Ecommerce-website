package com.se2.demo.controller;

import com.se2.demo.dto.request.ProductFilterRequest;
import com.se2.demo.dto.response.CartResponse;
import com.se2.demo.dto.response.OrderDetailResponse;
import com.se2.demo.dto.response.ProductResponse;
import com.se2.demo.model.entity.User;
import com.se2.demo.service.ProductService;
import com.se2.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // Để dùng @RequestParam
import java.util.ArrayList; // Để dùng ArrayList
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

import com.se2.demo.service.CartService;
import com.se2.demo.service.OrderService;

import java.security.Principal;
import java.util.List;

@Controller
public class WebController {

    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    public WebController(ProductService productService, CartService cartService, OrderService orderService, UserService userService) {
        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
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
            Principal principal,
            Model model) {

        // 1. Giả lập thông tin User (Giữ nguyên logic cũ của bạn)
        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userService.getUserByEmail(principal.getName());
        MockUser user = new MockUser(
                currentUser.getFullName(),
                currentUser.getEmail(),
                currentUser.getAvatarUrl());
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
                CartResponse cart = cartService.getCartByUserId(currentUser.getId());
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


    // so sánh sp
    @GetMapping("/product-comparison/details")
    public String showComparisonDetails(
            @RequestParam(value = "ids", required = false) String ids,
            Model model) {

        List<ProductResponse> compareList = new ArrayList<>();

        if (ids != null && !ids.isEmpty()) {
            try {
                // Tách chuỗi "1,2" và chuyển thành List<Integer> an toàn
                List<Integer> idList = java.util.Arrays.stream(ids.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .limit(4) // Giới hạn tối đa 4 sản phẩm để giao diện đẹp
                        .toList();

                for (Integer id : idList) {
                    ProductResponse product = productService.getProductById(id);
                    if (product != null) {
                        compareList.add(product);
                    }
                }
            } catch (NumberFormatException e) {
                // Nếu ID không phải là số, chỉ cần log và không làm sập trang
                System.err.println("Invalid ID format: " + e.getMessage());
            }
        }

        model.addAttribute("compareList", compareList);
        return "pages/product_compare_details";
    }
}
