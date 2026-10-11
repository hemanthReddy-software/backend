package com.aichat.service;

public interface EmailService {
    void sendVerificationCode(String email, String code);
}
