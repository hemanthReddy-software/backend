package com.aichat.controller;

import com.aichat.dto.AuthDtos.ChatRequest;
import com.aichat.dto.AuthDtos.ChatResponse;
import com.aichat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/message")
    public ResponseEntity<ChatResponse> message(
            @RequestBody ChatRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        return ResponseEntity.ok(chatService.ask(email, request));
    }
}
