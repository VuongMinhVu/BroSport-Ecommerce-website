package com.se2.demo.service.impl;

import com.se2.demo.model.entity.Order;
import com.se2.demo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    @Async("notificationExecutor")
    public void sendOrderSuccessEmail(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(order.getUser().getEmail());
            helper.setSubject("Xác nhận đơn hàng thành công - " + order.getOrderCode());

            // Nội dung HTML mô phỏng lại Order Summary trên Figma
            String htmlContent = "<h1>Cảm ơn bạn đã mua hàng tại BroSport!</h1>" +
                    "<p>Mã đơn hàng: " + order.getOrderCode() + "</p>" +
                    "<p>Tổng thanh toán: " + order.getTotalPrice() + " VND</p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Email sent successfully for Order: {}", order.getOrderCode());
        } catch (MessagingException e) {
            log.error("Failed to send email for Order: {}", order.getOrderCode(), e);
        }
    }

    @Override
    @Async("notificationExecutor")
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Mã OTP khôi phục mật khẩu - BroSport");

            String htmlContent = "<h2>Yêu cầu khôi phục mật khẩu</h2>" +
                    "<p>Mã OTP của bạn là: <b>" + otp + "</b></p>" +
                    "<p>Mã này dùng để xác minh tài khoản của bạn. Vui lòng không chia sẻ mã này cho bất kỳ ai.</p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Đã gửi email OTP thành công đến: {}", toEmail);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email OTP đến: {}", toEmail, e);
        }
    }
}