package com.se2.demo.controller;

import com.se2.demo.model.entity.ChatMessage;
import com.se2.demo.model.entity.MessageStatus;
import com.se2.demo.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {
  private final SimpMessagingTemplate messagingTemplate;
  private final ChatMessageRepository chatMessageRepository;

  @MessageMapping("/chat.sendMessage")
  public void processMessage(@Payload ChatMessage chatMessage, Principal principal) {
    String sender = (principal != null) ? principal.getName() : chatMessage.getSenderId();
    chatMessage.setSenderId(sender);

    if (chatMessage.getRecipientId() == null || chatMessage.getRecipientId().trim().isEmpty()) {
      chatMessage.setRecipientId("admin@brosport.com");
    }

    chatMessage.setTimestamp(LocalDateTime.now());
    chatMessage.setStatus(MessageStatus.RECEIVED);

    ChatMessage savedMsg = chatMessageRepository.save(chatMessage);

    messagingTemplate.convertAndSendToUser(
            chatMessage.getRecipientId(),
            "/queue/messages",
            savedMsg
    );
  }
}