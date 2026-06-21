package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private boolean isUser(
            HttpSession session) {

        return session.getAttribute("loginUser") != null
                && "USER".equals(session.getAttribute("role"));
    }

    @GetMapping("/user")
    public String user(HttpSession session) {

        // 一般ユーザーチェック
        if (!isUser(session)) {

            return "redirect:/login";
        }

        return "user";
    }
}
