package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/user")
    public String user(HttpSession session) {

        // 未ログインチェック
        if (session.getAttribute("loginUser") == null) {

            return "redirect:/login";
        }

        return "user";
    }
}