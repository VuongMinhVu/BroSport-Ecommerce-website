package com.se2.demo.controller;

import com.se2.demo.service.UserService;
import com.se2.demo.model.entity.User;
import java.security.Principal;
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

import com.se2.demo.service.CartService;
import com.se2.demo.service.OrderService;

@Controller
public class WebController {

    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    public WebController(ProductService productService, CartService cartService, OrderService orderService,
            UserService userService) {

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
                    filter.setGenderId(1);
                    break;
                case "women":
                    filter.setGenderId(2);
                    break;
                case "accessories":
                    filter.setCategoryId(3);
                    break;
                case "sports":
                    filter.setCategoryId(4);
                    break;
            }
            model.addAttribute("categorySlug", categorySlug);
        }

        if (filter.getPage() == null || filter.getPage() < 0)
            filter.setPage(0);
        if (filter.getSize() == null || filter.getSize() <= 0)
            filter.setSize(12);

        var products = productService.getAllProducts(filter);

        model.addAttribute("products", products.getContent());
        model.addAttribute("currentPage", products.getPageNo());
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalItems", products.getTotalElements());
        model.addAttribute("filter", filter);

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
    public String showCartDetail() {
        return "cart-detail";
    }

    public record CheckoutItemDTO(String name, String size, String color, int quantity, double price, String imageUrl) {
    }

    @GetMapping("/checkout")
    public String showCheckout(
            @RequestParam(required = false) Boolean buyNow,
            @RequestParam(required = false) Integer variantId,
            @RequestParam(required = false) Integer qty,

            Model model,
            Principal principal) {

        // 1. Chặn người dùng chưa đăng nhập
        if (principal == null) {
            return "redirect:/login";
        }

        // 2. Lấy User để phục vụ logic nghiệp vụ (tìm Giỏ hàng của đúng người này)
        // Không cần đẩy vào Model nữa vì GlobalControllerAdvice đã tự động đẩy rồi
        User currentUser = userService.getUserByEmail(principal.getName());

        List<CheckoutItemDTO> displayItems = new java.util.ArrayList<>();
        double subtotal = 0.0;

        if (Boolean.TRUE.equals(buyNow) && variantId != null) {
            // LUỒNG 1: MUA NGAY
            try {
                ProductResponse product = productService.getProductByVariantId(variantId);

                // Lớp bảo vệ chống NullPointerException
                if (product != null && product.getProductDetails() != null) {
                    var detail = product.getProductDetails().stream()
                            .filter(d -> variantId.equals(d.getId()))
                            .findFirst()
                            .orElse(null);

                    if (detail != null) {
                        String imgUrl = "";
                        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                            imgUrl = product.getProductImages().get(0).getImageUrl();
                        }

                        double itemPrice = product.getShowPrice() != null ? product.getShowPrice().doubleValue() : 0.0;

                        CheckoutItemDTO item = new CheckoutItemDTO(
                                product.getName(),
                                detail.getSize(),
                                detail.getColor(),
                                qty != null ? qty : 1,
                                itemPrice,
                                imgUrl);
                        displayItems.add(item);
                        subtotal = item.price() * item.quantity();
                    }
                }
            } catch (Exception e) {
                System.err.println(">>> Lỗi Mua Ngay: " + e.getMessage());
                return "redirect:/products";
            }
        } else {
            // LUỒNG 2: MUA TỪ GIỎ HÀNG
            try {
                CartResponse cart = cartService.getCartByUserId(currentUser.getId());
                displayItems = cart.getCartDetails().stream()
                        .map(detail -> new CheckoutItemDTO(
                                detail.getProductName(),
                                detail.getSizeName(),
                                detail.getColorName(),
                                detail.getQuantity(),
                                detail.getUnitPrice(),
                                detail.getImageUrl()))
                        .toList();
                subtotal = displayItems.stream().mapToDouble(item -> item.price() * item.quantity()).sum();
            } catch (Exception e) {
                System.err.println(">>> Lỗi Giỏ Hàng: " + e.getMessage());
                displayItems = List.of();
            }
        }

        double shippingFee = (subtotal > 0 && subtotal < 1000000) ? 30000.0 : 0.0;
        double total = subtotal + shippingFee;

        model.addAttribute("cartItems", displayItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("total", total);

        return "checkout";
    }

    @PostMapping("/checkout/process")
    public String processCheckout() {
        return "redirect:/order-success";
    }

    @GetMapping("/")
    public String showHomePage() {
        return "pages/homepage";
    }

    @GetMapping("/order-success")
    public String showOrderSuccess(@RequestParam(required = false) String orderCode, Model model) {
        if (orderCode != null && !orderCode.isEmpty()) {
            try {
                OrderDetailResponse order = orderService.getOrderDetail(orderCode);
                model.addAttribute("order", order);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "order-success";
    }

    @GetMapping("/order-history")
    public String orderHistoryPage(Principal principal) {
        // Đã xóa hết code thừa. GlobalControllerAdvice sẽ tự động lo việc truyền User.
        if (principal == null)
            return "redirect:/login";
        return "orderHistory";
    }

    @GetMapping("/product/{slug}")
    public String showProductDetailPage(@PathVariable("slug") String slug, Model model) {
        ProductResponse product = productService.getProductBySlug(slug);
        model.addAttribute("product", product);
        return "productDetail";
    }

    @GetMapping("/order-tracking")
    public String orderTrackingPage() {
        return "orderTracking";
    }

    @GetMapping("/search-result")
    public String searchResultPage() {
        return "search-result";
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
