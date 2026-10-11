package com.aichat.service;

import com.aichat.dto.AuthDtos.*;
import com.aichat.repository.UserRepository;
import com.aichat.repository.VerificationRepository;
import com.aichat.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SecureRandom random = new SecureRandom();

    public AuthServiceImpl(UserRepository userRepository,
                           VerificationRepository verificationRepository,
                           EmailService emailService,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public MessageResponse register(RegisterRequest request) {
        validateEmail(request.email());
        if (request.password() == null || request.password().length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        }

        if (userRepository.exists(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        String encoded = passwordEncoder.encode(request.password());
        Long userId = userRepository.register(request.email(), encoded);

        String otp = String.format("%06d", random.nextInt(1_000_000));
        verificationRepository.saveOtp(userId, otp);
        emailService.sendVerificationCode(request.email(), otp);

        return new MessageResponse("Verification code sent to your email");
    }

    @Override
    public MessageResponse verify(VerifyRequest request) {
        verificationRepository.verify(request.email(), request.code());
        return new MessageResponse("Email verified successfully");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        if (!userRepository.isVerified(request.email())) {
            throw new IllegalArgumentException("Please verify your email before login");
        }

        String storedPassword = userRepository.findPasswordByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), storedPassword)) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        Long userId = userRepository.findIdByEmail(request.email());
        String token = jwtUtil.generateToken(userId, request.email());

        return new LoginResponse(token, request.email());
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Enter a valid email address");
        }
    }
}
