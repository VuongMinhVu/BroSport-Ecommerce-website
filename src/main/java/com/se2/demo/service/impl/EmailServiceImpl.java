package com.se2.demo.service.impl;

import com.se2.demo.model.entity.Order;
import com.se2.demo.model.entity.OrderItem;
import com.se2.demo.repository.OrderRepository;
import com.se2.demo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final OrderRepository orderRepository;

    @Override
    @Async("notificationExecutor")
    @Transactional(readOnly = true)
    public void sendOrderSuccessEmail(Integer orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng để gửi mail"));

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(order.getUser().getEmail());
            helper.setSubject("BroSport - Đặt hàng thành công đơn " + order.getOrderCode());

            // header
            StringBuilder html = new StringBuilder();
            html.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #eee; border-radius: 10px;'>");
            html.append("<h2 style='color: #FF5A5F; text-align: center;'>Cảm ơn bạn đã mua hàng!</h2>");
            html.append("<p>Xin chào <b>").append(order.getFullName()).append("</b>,</p>");
            html.append("<p>BroSport đã nhận được đơn đặt hàng <b>").append(order.getOrderCode()).append("</b> của bạn.</p>");

            // bảng chi tiết sản phẩm
            html.append("<table style='width: 100%; border-collapse: collapse; margin-top: 20px;'>");
            html.append("<tr style='background-color: #f8f9fa; text-align: left;'>");
            html.append("<th style='padding: 10px; border: 1px solid #ddd;'>Sản phẩm</th>");
            html.append("<th style='padding: 10px; border: 1px solid #ddd;'>SL</th>");
            html.append("<th style='padding: 10px; border: 1px solid #ddd;'>Đơn giá</th>");
            html.append("</tr>");

            for (OrderItem item : order.getOrderItems()) {
                String productName = item.getProductDetail().getProduct().getName();
                String color = item.getProductDetail().getColor().getColorName() != null ? item.getProductDetail().getColor().getColorName() : "N/A";
                String size = item.getProductDetail().getSize().getSizeDescription() != null ? item.getProductDetail().getSize().getSizeDescription() : "N/A";

                html.append("<tr>");
                html.append("<td style='padding: 10px; border: 1px solid #ddd;'>")
                        .append(productName).append("<br/>")
                        .append("<small style='color: gray;'>Màu: ").append(color).append(" | Size: ").append(size).append("</small></td>");
                html.append("<td style='padding: 10px; border: 1px solid #ddd; text-align: center;'>").append(item.getQuantity()).append("</td>");
                html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(item.getPrice()).append(" VNĐ</td>");
                html.append("</tr>");
            }
            html.append("</table>");

            // Tổng kết tiền
            html.append("<h3 style='text-align: right; margin-top: 20px;'>Tổng thanh toán: <span style='color: #FF5A5F;'>").append(order.getTotalPrice()).append(" VNĐ</span></h3>");
            html.append("<p><b>Phương thức thanh toán:</b> ").append(order.getPaymentMethod()).append("</p>");
            html.append("<p><b>Địa chỉ nhận hàng:</b> ").append(order.getShippingAddressFull()).append("</p>");
            html.append("<hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'/>");
            html.append("<p style='text-align: center; color: gray; font-size: 12px;'>Đội ngũ BroSport xin chân thành cảm ơn.</p>");
            html.append("</div>");

            helper.setText(html.toString(), true);
            mailSender.send(message);
            log.info("Đã gửi email thành công cho đơn hàng: {}", order.getOrderCode());

        } catch (Exception e) {
            log.error("Lỗi gửi email cho đơn hàng ID: {}", orderId, e);
        }
    }

    @Override
    @Async("notificationExecutor")
    public void sendOtpEmail(String email, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("BroSport - Mã xác thực Quên mật khẩu");

            // --- VẼ GIAO DIỆN HTML CHO EMAIL OTP ---
            StringBuilder html = new StringBuilder();
            html.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #eee; border-radius: 10px;'>");
            html.append("<h2 style='color: #FF5A5F; text-align: center;'>Yêu cầu đặt lại mật khẩu</h2>");
            html.append("<p>Xin chào,</p>");
            html.append("<p>Hệ thống BroSport vừa nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Vui lòng nhập mã xác thực (OTP) dưới đây để hoàn tất:</p>");

            // Mã OTP được làm to, in đậm và có background nổi bật
            html.append("<div style='text-align: center; margin: 30px 0;'>");
            html.append("<span style='font-size: 32px; font-weight: bold; letter-spacing: 10px; color: #333; background: #f4f4f4; padding: 15px 30px; border-radius: 5px;'>")
                    .append(otp).append("</span>");
            html.append("</div>");

            html.append("<p style='color: red; font-size: 14px; text-align: center;'>Lưu ý: Mã xác thực này có hiệu lực trong phiên làm việc của bạn. TUYỆT ĐỐI KHÔNG chia sẻ mã này cho bất kỳ ai!</p>");
            html.append("<hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'/>");
            html.append("<p style='text-align: center; color: gray; font-size: 12px;'>Nếu bạn không yêu cầu đổi mật khẩu, vui lòng bỏ qua email này.</p>");
            html.append("</div>");

            helper.setText(html.toString(), true);
            mailSender.send(message);
            log.info("Đã gửi email OTP thành công tới: {}", email);

        } catch (Exception e) {
            log.error("Lỗi gửi email OTP tới: {}", email, e);
        }
    }
}