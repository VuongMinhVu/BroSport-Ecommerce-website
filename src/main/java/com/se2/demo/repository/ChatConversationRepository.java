package com.se2.demo.repository;

import com.se2.demo.model.entity.ChatConversation;
import com.se2.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
  @Query("SELECT c FROM ChatConversation c JOIN FETCH c.user ORDER BY c.updatedAt DESC")
  List<ChatConversation> findAllConversationsWithUser();

  List<ChatConversation> findAllByOrderByUpdatedAtDesc();

  Optional<ChatConversation> findByUser(User user);
}