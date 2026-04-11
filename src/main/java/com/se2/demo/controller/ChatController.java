package com.se2.demo.controller;

import com.se2.demo.model.entity.ChatMessage;
import com.se2.demo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  @Autowired
  private ChatService chatService;

  @MessageMapping("/chat.sendMessage")
  public void processMessage(@Payload ChatMessage incomingMsg, Principal principal) {
    String senderEmail;
    if (principal == null) {
      System.out.println("⚠️ CẢNH BÁO: Đang cho phép gửi tin nhắn nặc danh (Chưa đăng nhập)! Hãy chặn lại khi lên Production.");
      senderEmail = incomingMsg.getSenderEmail();
    } else {
      senderEmail = principal.getName();
    }
    
    String recipientEmail = incomingMsg.getRecipientId(); // Nếu user gửi, recipient = "admin"

    // 1. Lưu vào Database thông qua Service
    ChatMessage savedMsg = chatService.saveMessage(senderEmail, recipientEmail, incomingMsg.getContent());

    // 2. Gửi tin nhắn real-time tới người nhận
    messagingTemplate.convertAndSend(
            "/topic/messages/" + recipientEmail,
            savedMsg
    );
  }
}