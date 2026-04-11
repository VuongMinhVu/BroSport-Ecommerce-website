package com.se2.demo.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder
public class MessageResponse {
  private Long id;
  private String senderEmail;
  private String content;
  private LocalDateTime timestamp;
  private boolean isRead;
}