package com.aichat.dto;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(String email, String password) {}
    public record VerifyRequest(String email, String code) {}
    public record LoginRequest(String email, String password) {}
    public record LoginResponse(String token, String email) {}
    public record MessageResponse(String message) {}
    public record ChatRequest(Long chatId, String message) {}
    public record ChatResponse(Long chatId, String message) {}
}
