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
    @Transactional
    public OrderResponse checkout(Integer userId, OrderRequest request, HttpServletRequest httpServletRequest) {
        // 1. Lấy giỏ hàng
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng trống!"));

        // 2. Tính toán
        double subtotalVal = cart.getCartDetails().stream()
                .mapToDouble(d -> d.getProductDetail().getProduct().getShowPrice().doubleValue() * d.getQuantity())
                .sum();

        BigDecimal subtotal = new BigDecimal(subtotalVal);
        BigDecimal shippingFee = new BigDecimal(30000);
        BigDecimal discount = calculateDiscount(request.getVoucherCode(), subtotalVal);
        BigDecimal finalAmount = subtotal.add(shippingFee).subtract(discount);


        String method = request.getPaymentMethod().toUpperCase();
        switch (method) {
            case "COD":
                return processCODOrder(request, cart, finalAmount, shippingFee, subtotal);
            case "VNPAY":
                return processVNPayRequest(request, cart, finalAmount, shippingFee, subtotal, httpServletRequest);
            default:
                throw new RuntimeException("Phương thức thanh toán không hợp lệ!");
        }
    }

    // COD
    private OrderResponse processCODOrder(OrderRequest request, Cart cart, BigDecimal total, BigDecimal ship, BigDecimal subtotal) {
        Order order = Order.builder()
                .orderCode("BS" + System.currentTimeMillis())
                .user(cart.getUser())
                .fullName(request.getFullName())
                .shippingAddressFull(request.getShippingAddress())
                .totalPrice(total)
                .shippingFee(ship)
                .paymentMethod("COD")
                .paymentStatus("UNPAID")
                .orderStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        List<OrderItem> items = cart.getCartDetails().stream().map(d -> {
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

        recreateEmptyCart(cart.getUser());
        cartRepository.delete(cart);

        emailService.sendOrderSuccessEmail(savedOrder);

        OrderResponse response = orderMapper.toResponse(savedOrder);
        response.setPaymentMethod("COD");
        response.setMessage("Đặt hàng COD thành công!");
        return response;
    }

    // VNPAY
    private OrderResponse processVNPayRequest(OrderRequest request, Cart cart, BigDecimal total, BigDecimal ship, BigDecimal subtotal, HttpServletRequest httpServletRequest) {
        Order order = Order.builder()
                .orderCode("BS" + System.currentTimeMillis())
                .user(cart.getUser())
                .fullName(request.getFullName())
                .shippingAddressFull(request.getShippingAddress())
                .totalPrice(total)
                .shippingFee(ship)
                .paymentMethod("VNPAY")
                .paymentStatus("PENDING")
                .orderStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        List<OrderItem> items = cart.getCartDetails().stream().map(d -> OrderItem.builder()
                .order(order)
                .productDetail(d.getProductDetail())
                .quantity(d.getQuantity())
                .price(d.getProductDetail().getProduct().getShowPrice())
                .build()).collect(Collectors.toList());

        order.setOrderItems(items);
        Order savedOrder = orderRepository.save(order);

        String baseUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort();
        String paymentUrl = vnPayService.createPaymentUrl(total, savedOrder.getOrderCode(), baseUrl);


        OrderResponse response = orderMapper.toResponse(savedOrder);
        response.setPaymentUrl(paymentUrl);
        response.setPaymentMethod("VNPAY");
        response.setMessage("Vui lòng thực hiện thanh toán qua VNPay");
        return response;
    }

    @Override
    @Transactional
    public OrderResponse processPaymentCallback(HttpServletRequest request) {
        int status = vnPayService.validatePayment(request);
        if (status != 1) {
            throw new RuntimeException("Thanh toán thất bại hoặc sai chữ ký!");
        }

        String orderCode = request.getParameter("vnp_TxnRef");
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với mã: " + orderCode));

        order.setPaymentStatus("PAID");

        User user = order.getUser();
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);

        if (cart != null) {
            order.getOrderItems().forEach(item -> {
                ProductDetail pd = item.getProductDetail();
                if (pd.getStockQuantity() < item.getQuantity()) {
                    throw new RuntimeException("Sản phẩm " + pd.getProduct().getName() + " đã hết hàng!");
                }
                pd.setStockQuantity(pd.getStockQuantity() - item.getQuantity());
                productDetailRepository.save(pd);
            });

            recreateEmptyCart(user);
            cartRepository.delete(cart);
        }
        orderRepository.save(order);

        emailService.sendOrderSuccessEmail(order);

        OrderResponse response = orderMapper.toResponse(order);
        response.setPaymentMethod("VNPAY");
        response.setMessage("Thanh toán VNPay thành công!");

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
        if (code == null || code.isEmpty()) return BigDecimal.ZERO;
        return voucherRepository.findByCodeAndIsActiveTrue(code)
                .filter(v -> subtotal >= v.getMinOrderValue().doubleValue())
                .map(Voucher::getDiscountValue).orElse(BigDecimal.ZERO);
    }

    @Override
    public List<OrderHistoryResponse> getOrderHistory(Integer userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orderMapper.toHistoryResponseList(orders);
    }

    @Override
    public OrderDetailResponse getOrderDetail(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderCode));
        return orderMapper.toDetailResponse(order);
    }
}