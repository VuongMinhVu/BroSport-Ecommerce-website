package com.se2.demo.service.impl;

import com.se2.demo.dto.request.OrderRequest;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductDetailRepository productDetailRepository;
    private final VoucherRepository voucherRepository;
    private final VNPayService vnPayService;
    private final EmailService emailService;
    private final OrderMapper orderMapper;

    @Override
    public String preparePayment(OrderRequest request, HttpServletRequest httpServletRequest) {
        // Lấy giỏ hàng để tính tiền
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Giỏ hàng trống!"));

        // Tính subtotal từ giỏ hàng
        double subtotal = cart.getCartDetails().stream()
                .mapToDouble(d -> d.getProductDetail().getProduct().getShowPrice().doubleValue() * d.getQuantity())
                .sum();

        // 1.  Tính tổng tiền cuối cùng
        BigDecimal finalAmount = new BigDecimal(subtotal).add(new BigDecimal(30000)); // Ví dụ phí ship 30k

        // logic apply voucher
        BigDecimal discount = BigDecimal.ZERO;
        if (request.getVoucherCode() != null) {
            Voucher voucher = voucherRepository.findByCodeAndIsActiveTrue(request.getVoucherCode())
                    .orElse(null);
            if (voucher != null && subtotal >= voucher.getMinOrderValue().doubleValue()) {
                discount = voucher.getDiscountValue();
            }
        }
        finalAmount = finalAmount.subtract(discount);

        // 2. Sửa lỗi 'baseUrl': Lấy từ request
        String baseUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort();

        // 3. Sửa lỗi 'createOrder' -> đổi thành 'createPaymentUrl' theo Interface mới
        return vnPayService.createPaymentUrl(finalAmount, request.getUserId().toString(), baseUrl);
    }

    @Override
    @Transactional
    public OrderResponse processPaymentCallback(HttpServletRequest request) {
        // 4. Sửa lỗi 'orderReturn' -> đổi thành 'validatePayment' theo Interface mới
        int status = vnPayService.validatePayment(request);
        if (status != 1) throw new RuntimeException("Thanh toán thất bại!");

        // 5. Giải mã userId từ OrderInfo
        Integer userId = Integer.parseInt(request.getParameter("vnp_OrderInfo"));
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng!"));

        // 6. Tạo Order & Items
        Order order = Order.builder()
                .orderCode(request.getParameter("vnp_TxnRef"))
                .user(cart.getUser())
                .totalPrice(new BigDecimal(request.getParameter("vnp_Amount"))
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP))
                .paymentMethod("VNPAY")
                .createdAt(LocalDateTime.now())
                .build();

        List<OrderItem> items = cart.getCartDetails().stream().map(d -> {
            // Logic trừ kho
            ProductDetail pd = d.getProductDetail();
            pd.setStockQuantity(pd.getStockQuantity() - d.getQuantity());
            productDetailRepository.save(pd);

            return OrderItem.builder()
                    .order(order)
                    .productDetail(pd)
                    .quantity(d.getQuantity())
                    .price(pd.getProduct().getShowPrice())
                    .build();
        }).collect(Collectors.toList());

        order.setOrderItems(items);
        Order savedOrder = orderRepository.save(order);

        // 7. Sửa lỗi 'deleteByUserId' -> dùng phương thức có sẵn delete(cart)
        cartRepository.delete(cart);

        // 8. Sửa lỗi 'sendOrderConfirmation' -> đổi thành 'sendOrderSuccessEmail' theo Interface mới
        emailService.sendOrderSuccessEmail(savedOrder);

        return orderMapper.toResponse(savedOrder);
    }
}