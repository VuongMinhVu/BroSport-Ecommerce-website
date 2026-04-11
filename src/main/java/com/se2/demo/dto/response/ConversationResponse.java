package com.se2.demo.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder
public class ConversationResponse {
  private Long id;
  private String userEmail;
  private String userFullName;
  private String lastMessage;
  private int unreadCount;
  private LocalDateTime updatedAt;
}