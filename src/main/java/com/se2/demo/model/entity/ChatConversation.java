package com.se2.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat_conversations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatConversation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  private int userUnreadCount;
  private int adminUnreadCount;

  private String lastMessage;
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ChatMessage> messages;
}