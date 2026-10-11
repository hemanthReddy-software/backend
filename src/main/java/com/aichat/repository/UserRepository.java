package com.aichat.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Long register(String email, String encodedPassword) {
        return jdbc.queryForObject(
                "SELECT register_user(?, ?) FROM dual",
                Long.class,
                email,
                encodedPassword
        );
    }

    public String findPasswordByEmail(String email) {
        return jdbc.queryForObject(
                "SELECT password FROM users WHERE LOWER(email)=LOWER(?)",
                String.class,
                email
        );
    }

    public Long findIdByEmail(String email) {
        return jdbc.queryForObject(
                "SELECT user_id FROM users WHERE LOWER(email)=LOWER(?)",
                Long.class,
                email
        );
    }

    public boolean isVerified(String email) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE LOWER(email)=LOWER(?) AND email_verified='Y'",
                Integer.class,
                email
        );
        return count != null && count > 0;
    }

    public boolean exists(String email) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE LOWER(email)=LOWER(?)",
                Integer.class,
                email
        );
        return count != null && count > 0;
    }
}
