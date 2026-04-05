package com.se2.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String senderId;
  private String recipientId;

  private String content;
  private LocalDateTime timestamp;

  @Enumerated(EnumType.STRING)
  private MessageStatus status;
}