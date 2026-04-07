package com.se2.demo.service.impl;

import com.se2.demo.dto.request.OrderRequest;
import com.se2.demo.dto.response.OrderDetailResponse;
import com.se2.demo.dto.response.OrderHistoryResponse;
import com.se2.demo.dto.response.OrderResponse;
import com.se2.demo.mapper.OrderMapper;
import com.se2.demo.model.entity.*;
import com.se2.demo.repository.*;
import com.se2.demo.service.EmailService;
import com.se2.demo.service.OrderService;
import com.se2.demo.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductDetailRepository productDetailRepository;
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository; // THÊM REPOSITORY NÀY
    private final VNPayService vnPayService;
    private final EmailService emailService;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse checkout(Integer userId, OrderRequest request, HttpServletRequest httpServletRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));

        // ĐÃ XÓA ĐOẠN TÌM GIỎ HÀNG BỊ THỪA Ở ĐÂY

        double subtotalVal = 0;
        List<OrderItem> orderItems = new ArrayList<>();
        Cart cartToDelete = null;

        // KIỂM TRA LUỒNG MUA HÀNG
        if (Boolean.TRUE.equals(request.getIsBuyNow())) {
            // 1. Luồng MUA NGAY (Không quan tâm đến giỏ hàng)
            ProductDetail pd = productDetailRepository.findById(request.getProductDetailId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

            subtotalVal = pd.getProduct().getShowPrice().doubleValue() * request.getQuantity();
            OrderItem item = OrderItem.builder()
                    .productDetail(pd)
                    .quantity(request.getQuantity())
                    .price(pd.getProduct().getShowPrice())
                    .build();
            orderItems.add(item);
        } else {
            // 2. Luồng MUA TỪ GIỎ HÀNG (Chỉ tìm giỏ hàng khi rơi vào luồng này)
            cartToDelete = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng!"));

            // Bổ sung lớp bảo vệ: Nếu có giỏ hàng nhưng bên trong không có sản phẩm nào
            if (cartToDelete.getCartDetails() == null || cartToDelete.getCartDetails().isEmpty()) {
                throw new RuntimeException("Giỏ hàng của bạn đang trống!");
            }

            subtotalVal = cartToDelete.getCartDetails().stream()
                    .mapToDouble(d -> d.getProductDetail().getProduct().getShowPrice().doubleValue() * d.getQuantity())
                    .sum();

            for (CartDetail d : cartToDelete.getCartDetails()) {
                OrderItem item = OrderItem.builder()
                        .productDetail(d.getProductDetail())
                        .quantity(d.getQuantity())
                        .price(d.getProductDetail().getProduct().getShowPrice())
                        .build();
                orderItems.add(item);
            }
        }

        BigDecimal subtotal = new BigDecimal(subtotalVal);

        // Tính phí ship (Miễn phí nếu tổng > 1.000.000đ)
        BigDecimal shippingFee = (subtotalVal > 0 && subtotalVal < 1000000)
                ? new BigDecimal(30000)
                : BigDecimal.ZERO;

        BigDecimal discount = calculateDiscount(request.getVoucherCode(), subtotalVal);

        BigDecimal finalAmount = subtotal.add(shippingFee).subtract(discount);

        String method = request.getPaymentMethod().toUpperCase();
        if ("COD".equals(method)) {
            return processCODOrder(request, user, orderItems, cartToDelete, finalAmount, shippingFee, discount);
        } else if ("VNPAY".equals(method)) {
            return processVNPayRequest(request, user, orderItems, cartToDelete, finalAmount, shippingFee, discount,
                    httpServletRequest);
        } else {
            throw new RuntimeException("Phương thức thanh toán không hợp lệ!");
        }
    }

    private OrderResponse processCODOrder(OrderRequest request, User user, List<OrderItem> items, Cart cartToDelete,
            BigDecimal total, BigDecimal ship, BigDecimal discount) {
        Order order = createBaseOrder(request, user, total, ship, discount, "COD", "UNPAID");

        // Trừ tồn kho
        items.forEach(item -> {
            item.setOrder(order);
            ProductDetail pd = item.getProductDetail();
            pd.setStockQuantity(pd.getStockQuantity() - item.getQuantity());
            productDetailRepository.save(pd);
        });

        order.setOrderItems(items);
        Order savedOrder = orderRepository.save(order);

        // Chỉ xóa giỏ hàng nếu mua từ giỏ hàng
        if (cartToDelete != null) {
            recreateEmptyCart(user);
            cartRepository.delete(cartToDelete);
        }

        emailService.sendOrderSuccessEmail(savedOrder);

        OrderResponse response = orderMapper.toResponse(savedOrder);
        response.setPaymentMethod("COD");
        response.setMessage("Đặt hàng COD thành công!");
        return response;
    }

    private OrderResponse processVNPayRequest(OrderRequest request, User user, List<OrderItem> items, Cart cartToDelete,
            BigDecimal total, BigDecimal ship, BigDecimal discount, HttpServletRequest httpServletRequest) {
        Order order = createBaseOrder(request, user, total, ship, discount, "VNPAY", "PENDING");

        items.forEach(item -> item.setOrder(order));
        order.setOrderItems(items);
        Order savedOrder = orderRepository.save(order);

        String baseUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":"
                + httpServletRequest.getServerPort();
        String paymentUrl = vnPayService.createPaymentUrl(total, savedOrder.getOrderCode(), baseUrl);

        OrderResponse response = orderMapper.toResponse(savedOrder);
        response.setPaymentUrl(paymentUrl);
        response.setPaymentMethod("VNPAY");
        return response;
    }

    private Order createBaseOrder(OrderRequest request, User user, BigDecimal total, BigDecimal ship,
            BigDecimal discount, String method,
            String paymentStatus) {
        return Order.builder()
                .orderCode("BS" + System.currentTimeMillis())
                .user(user)
                .fullName(request.getFullName())
                .shippingAddressFull(request.getShippingAddress())
                .totalPrice(total)
                .shippingFee(ship)
                .discountPrice(discount)
                .paymentMethod(method)
                .paymentStatus(paymentStatus)
                .orderStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public OrderResponse processPaymentCallback(HttpServletRequest request) {
        int status = vnPayService.validatePayment(request);
        if (status != 1)
            throw new RuntimeException("Thanh toán thất bại hoặc sai chữ ký!");

        String orderCode = request.getParameter("vnp_TxnRef");
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderCode));

        order.setPaymentStatus("PAID");

        // Trừ tồn kho khi thanh toán thành công
        order.getOrderItems().forEach(item -> {
            ProductDetail pd = item.getProductDetail();
            pd.setStockQuantity(pd.getStockQuantity() - item.getQuantity());
            productDetailRepository.save(pd);
        });

        // Xóa giỏ hàng nếu đơn hàng này được tạo từ giỏ hàng (check số lượng items
        // trong giỏ)
        Cart cart = cartRepository.findByUserId(order.getUser().getId()).orElse(null);
        if (cart != null && !cart.getCartDetails().isEmpty()) {
            recreateEmptyCart(order.getUser());
            cartRepository.delete(cart);
        }

        orderRepository.save(order);
        emailService.sendOrderSuccessEmail(order);

        OrderResponse response = orderMapper.toResponse(order);
        response.setPaymentMethod("VNPAY");
        return response;
    }

    private void recreateEmptyCart(User user) {
        Cart newEmptyCart = new Cart();
        newEmptyCart.setUser(user);
        newEmptyCart.setCreatedAt(LocalDateTime.now());
        newEmptyCart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(newEmptyCart);
    }

    private BigDecimal calculateDiscount(String code, double subtotal) {
        if (code == null || code.isEmpty())
            return BigDecimal.ZERO;
        return voucherRepository.findByCodeAndIsActiveTrue(code)
                .filter(v -> subtotal >= v.getMinOrderValue().doubleValue())
                .map(Voucher::getDiscountValue).orElse(BigDecimal.ZERO);
    }

    @Override
    public List<OrderHistoryResponse> getOrderHistory(Integer userId) {
        return orderMapper.toHistoryResponseList(orderRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @Override
    public OrderDetailResponse getOrderDetail(String orderCode) {
        return orderMapper.toDetailResponse(orderRepository.findByOrderCode(orderCode).orElseThrow());
    }
}