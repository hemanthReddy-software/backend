package com.aichat.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChatRepository {

    private final JdbcTemplate jdbc;

    public ChatRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Long createChat(Long userId, String title) {
        return jdbc.queryForObject(
                "SELECT create_chat(?, ?) FROM dual",
                Long.class,
                userId,
                title
        );
    }

    public void saveMessage(Long chatId, String role, String message) {
        jdbc.update("CALL save_chat_message(?, ?, ?)", chatId, role, message);
    }

    public boolean belongsToUser(Long chatId, Long userId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM chats WHERE chat_id=? AND user_id=?",
                Integer.class,
                chatId, userId
        );
        return count != null && count > 0;
    }
}
