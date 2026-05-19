package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    // ログイン画面表示
    @GetMapping("/login")
    public String login() {

        return "login";
    }

    // ログイン処理
    @PostMapping("/login")
    public String doLogin(
            @RequestParam String userId,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        // 管理者ログイン
        if (userId.equals("admin")
                && password.equals("1234abcd")) {

            session.setAttribute(
                    "loginUser",
                    userId);

            session.setAttribute(
                    "role",
                    "ADMIN");

            return "redirect:/admin";
        }
     // 一般ユーザー
        if (userId.equals("user")
                && password.equals("1234abcd")) {

            session.setAttribute(
                    "loginUser",
                    userId);

            session.setAttribute(
                    "role",
                    "USER");

            return "redirect:/user";
        }

        model.addAttribute(
                "errorMessage",
                "IDまたはパスワードが違います");

        return "login";
    }

    // ログアウト
    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}