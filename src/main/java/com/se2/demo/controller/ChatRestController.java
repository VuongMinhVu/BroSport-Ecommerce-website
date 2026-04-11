package com.se2.demo.controller;

import com.se2.demo.dto.response.ConversationResponse;
import com.se2.demo.dto.response.MessageResponse;
import com.se2.demo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

  @Autowired
  private ChatService chatService;

  @GetMapping("/conversations")
  public ResponseEntity<List<ConversationResponse>> getConversations() {
    return ResponseEntity.ok(chatService.getConversationsForAdmin());
  }

  @GetMapping("/my-conversation")
  public ResponseEntity<ConversationResponse> getMyConversation(java.security.Principal principal) {
    if (principal == null) {
      return ResponseEntity.status(401).build();
    }
    return ResponseEntity.ok(chatService.getMyConversation(principal.getName()));
  }

  @GetMapping("/history/{conversationId}")
  public ResponseEntity<List<MessageResponse>> getHistory(@PathVariable Long conversationId) {
    return ResponseEntity.ok(chatService.getMessageHistory(conversationId));
  }

  @PostMapping("/read/{conversationId}")
  public ResponseEntity<?> markAsRead(@PathVariable Long conversationId, @RequestParam boolean isAdmin) {
    chatService.markAsRead(conversationId, isAdmin);
    return ResponseEntity.ok("Marked as read");
  }
}