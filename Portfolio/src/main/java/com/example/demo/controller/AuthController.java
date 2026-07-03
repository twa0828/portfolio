package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AccountRepository accountRepository;

    public AuthController(
            AccountRepository accountRepository) {

        this.accountRepository = accountRepository;
    }

    @GetMapping("/login")
    public String login() {

        return "login";
    }

    @PostMapping("/login")
    public String doLogin(
            @RequestParam String userId,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        if (userId == null
                || userId.isBlank()) {

            model.addAttribute(
                    "errorMessage",
                    "メールアドレスを入力してください");

            return "login";
        }

        if (userId.length() > 255) {

            model.addAttribute(
                    "errorMessage",
                    "メールアドレスは255文字以内で入力してください");

            return "login";
        }

        if (password == null
                || password.isBlank()) {

            model.addAttribute(
                    "errorMessage",
                    "パスワードを入力してください");

            return "login";
        }

        if (!password.matches(
                "^[a-zA-Z0-9_-]{8,32}$")) {

            model.addAttribute(
                    "errorMessage",
                    "パスワードは8〜32文字の半角英数字と_-のみ使用できます");

            return "login";
        }

        Account account =
                accountRepository.findByEmail(userId)
                        .orElse(null);

        if (account == null
                || !account.getPassword().equals(password)) {

            model.addAttribute(
                    "errorMessage",
                    "IDまたはパスワードが違います");

            return "login";
        }

        if (!"アクセス許可".equals(account.getStatus())) {

            model.addAttribute(
                    "errorMessage",
                    "このアカウントはアクセス禁止です");

            return "login";
        }

        if ("true".equals(account.getDeleted())) {

            model.addAttribute(
                    "errorMessage",
                    "このアカウントは削除されています");

            return "login";
        }

        session.setAttribute(
                "loginUser",
                account.getEmail());

        session.setAttribute(
                "role",
                account.getRole());

        if ("ADMIN".equals(account.getRole())) {

            return "redirect:/admin";
        }

        if ("USER".equals(account.getRole())) {

            return "redirect:/user";
        }

        model.addAttribute(
                "errorMessage",
                "権限が不正です");

        return "login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}
