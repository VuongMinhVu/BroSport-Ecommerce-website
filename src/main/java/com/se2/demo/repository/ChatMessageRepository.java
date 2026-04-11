package com.se2.demo.repository;

import com.se2.demo.model.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  List<ChatMessage> findBySenderEmailAndRecipientIdOrSenderEmailAndRecipientIdOrderByTimestampAsc(
          String senderEmail1, String recipientId1,
          String senderEmail2, String recipientId2
  );

  Page<ChatMessage> findByConversationIdOrderByTimestampDesc(Long conversationId, Pageable pageable);
}