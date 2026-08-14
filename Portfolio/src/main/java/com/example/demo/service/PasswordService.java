package com.example.demo.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public String encode(
            String rawPassword) {

        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(
            String rawPassword,
            String savedPassword) {

        if (isBCrypt(savedPassword)) {

            return passwordEncoder.matches(
                    rawPassword,
                    savedPassword);
        }

        return rawPassword.equals(savedPassword);
    }

    public boolean needsHashing(
            String savedPassword) {

        return !isBCrypt(savedPassword);
    }

    private boolean isBCrypt(
            String password) {

        if (password == null) {

            return false;
        }

        return password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$");
    }
}
