package com.aichat.service;

import com.aichat.dto.AuthDtos.*;

public interface AuthService {
    MessageResponse register(RegisterRequest request);
    MessageResponse verify(VerifyRequest request);
    LoginResponse login(LoginRequest request);
}
