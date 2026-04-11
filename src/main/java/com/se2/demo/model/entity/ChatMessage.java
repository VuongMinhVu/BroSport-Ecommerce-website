package com.se2.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_conv_time", columnList = "conversation_id, timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id")
  private ChatConversation conversation;

  private String senderEmail;

  private String recipientId;

  private String content;
  private LocalDateTime timestamp;

  @Builder.Default
  private boolean isRead = false;

  @Enumerated(EnumType.STRING)
  private MessageStatus status;
}