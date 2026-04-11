package com.se2.demo.service;

import com.se2.demo.dto.response.ConversationResponse;
import com.se2.demo.dto.response.MessageResponse;
import com.se2.demo.model.entity.ChatConversation;
import com.se2.demo.model.entity.ChatMessage;
import com.se2.demo.model.entity.User;
import com.se2.demo.repository.ChatConversationRepository;
import com.se2.demo.repository.ChatMessageRepository;
import com.se2.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

  @Autowired
  private ChatConversationRepository conversationRepo;

  @Autowired
  private ChatMessageRepository messageRepo;

  @Autowired
  private UserRepository userRepo;

  @Transactional
  public ChatMessage saveMessage(String senderEmail, String recipientEmail, String content) {
    User clientUser = userRepo.findByEmail(senderEmail).orElse(null);
    boolean isAdminSending = false;

    if (clientUser == null || clientUser.getRole().equals("ROLE_ADMIN")) {
      clientUser = userRepo.findByEmail(recipientEmail).orElseThrow();
      isAdminSending = true;
    }

    User finalClientUser = clientUser;
    ChatConversation conversation = conversationRepo.findByUser(clientUser)
            .orElseGet(() -> ChatConversation.builder()
                    .user(finalClientUser)
                    .userUnreadCount(0)
                    .adminUnreadCount(0)
                    .build());

    conversation.setLastMessage(content);
    conversation.setUpdatedAt(LocalDateTime.now());
    if (isAdminSending) {
      conversation.setUserUnreadCount(conversation.getUserUnreadCount() + 1);
    } else {
      conversation.setAdminUnreadCount(conversation.getAdminUnreadCount() + 1);
    }
    conversationRepo.save(conversation);

    ChatMessage message = ChatMessage.builder()
            .conversation(conversation)
            .senderEmail(senderEmail)
            .content(content)
            .timestamp(LocalDateTime.now())
            .isRead(false)
            .build();

    return messageRepo.save(message);
  }

  public List<MessageResponse> getMessageHistory(Long conversationId) {
    org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 50);
    List<ChatMessage> pagedMessages = messageRepo.findByConversationIdOrderByTimestampDesc(conversationId, pageable).getContent();
    
    // Reverse to show chronological order in UI
    List<ChatMessage> mutableMessages = new java.util.ArrayList<>(pagedMessages);
    java.util.Collections.reverse(mutableMessages);

    return mutableMessages.stream().map(msg -> MessageResponse.builder()
            .id(msg.getId())
            .senderEmail(msg.getSenderEmail())
            .content(msg.getContent())
            .timestamp(msg.getTimestamp())
            .isRead(msg.isRead())
            .build()).collect(Collectors.toList());
  }

  public List<ConversationResponse> getConversationsForAdmin() {
    return conversationRepo.findAllConversationsWithUser().stream().map(conv ->
            ConversationResponse.builder()
                    .id(conv.getId())
                    .userEmail(conv.getUser().getEmail())
                    .userFullName(conv.getUser().getFullName())
                    .lastMessage(conv.getLastMessage())
                    .unreadCount(conv.getAdminUnreadCount())
                    .updatedAt(conv.getUpdatedAt())
                    .build()
    ).collect(Collectors.toList());
  }

  @Transactional
  public void markAsRead(Long conversationId, boolean isAdmin) {
    ChatConversation conversation = conversationRepo.findById(conversationId).orElseThrow();
    if (isAdmin) {
      conversation.setAdminUnreadCount(0);
    } else {
      conversation.setUserUnreadCount(0);
    }
    conversationRepo.save(conversation);
  }
}