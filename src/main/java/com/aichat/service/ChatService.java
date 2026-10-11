package com.aichat.service;

import com.aichat.dto.AuthDtos.ChatRequest;
import com.aichat.dto.AuthDtos.ChatResponse;
import com.aichat.repository.ChatRepository;
import com.aichat.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final OpenAiService openAiService;

    public ChatService(ChatRepository chatRepository,
                       UserRepository userRepository,
                       OpenAiService openAiService) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.openAiService = openAiService;
    }

    public ChatResponse ask(String email, ChatRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        Long userId = userRepository.findIdByEmail(email);
        Long chatId = request.chatId();

        if (chatId == null) {
            chatId = chatRepository.createChat(
                    userId,
                    request.message().length() > 40
                            ? request.message().substring(0, 40)
                            : request.message()
            );
        } else if (!chatRepository.belongsToUser(chatId, userId)) {
            throw new IllegalArgumentException("Chat does not belong to this user");
        }

        chatRepository.saveMessage(chatId, "user", request.message());

        String answer = openAiService.ask(request.message());

        chatRepository.saveMessage(chatId, "assistant", answer);

        return new ChatResponse(chatId, answer);
    }
}
