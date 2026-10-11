package com.aichat.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VerificationRepository {

    private final JdbcTemplate jdbc;

    public VerificationRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void saveOtp(Long userId, String otp) {
        jdbc.update("CALL create_email_verification(?, ?)", userId, otp);
    }

    public void verify(String email, String otp) {
        jdbc.update("CALL verify_email(?, ?)", email, otp);
    }
}
