package com.se2.demo.service;

import com.se2.demo.model.entity.Order;

public interface EmailService {
    // Gửi email xác nhận đơn hàng thành công kèm danh sách sản phẩm
    void sendOrderSuccessEmail(Order order);
}