package com.example.demo.controller;

import java.util.regex.Pattern;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.PasswordService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SettingController {

    private final AccountRepository accountRepository;
    private final PasswordService passwordService;

    public SettingController(
            AccountRepository accountRepository,
            PasswordService passwordService) {

        this.accountRepository = accountRepository;
        this.passwordService = passwordService;
    }

    private boolean isUser(
            HttpSession session) {

        return session.getAttribute("loginUser") != null
                && "USER".equals(session.getAttribute("role"));
    }

    @GetMapping("/settings")
    public String settings(
            HttpSession session,
            Model model) {

        if (!isUser(session)) {

            return "redirect:/login";
        }

        Account account =
                getLoginAccount(session);

        if (account == null) {

            return "redirect:/login";
        }

        model.addAttribute(
                "account",
                account);

        model.addAttribute(
                "emailInput",
                account.getEmail());

        return "settings";
    }

    @PostMapping("/settings/update")
    public String updateSettings(

            @RequestParam String email,
            @RequestParam String password,

            HttpSession session,
            Model model) {

        if (!isUser(session)) {

            return "redirect:/login";
        }

        Account account =
                getLoginAccount(session);

        if (account == null) {

            return "redirect:/login";
        }

        model.addAttribute(
                "account",
                account);

        model.addAttribute("emailInput", email);
        model.addAttribute("passwordInput", password);

        if (email.length() > 255) {

            model.addAttribute(
                    "errorMessage",
                    "メールアドレスは255文字以内で入力してください");

            return "settings";
        }

        if (!Pattern.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
                email)) {

            model.addAttribute(
                    "errorMessage",
                    "メールアドレス形式が不正です");

            return "settings";
        }

        Account sameEmailAccount =
                accountRepository.findByEmail(email)
                        .orElse(null);

        if (sameEmailAccount != null
                && !sameEmailAccount.getId().equals(account.getId())) {

            model.addAttribute(
                    "errorMessage",
                    "このメールアドレスは既に登録されています");

            return "settings";
        }

        if (password != null
                && !password.isBlank()
                && !password.matches(
                        "^[a-zA-Z0-9_-]{8,32}$")) {

            model.addAttribute(
                    "errorMessage",
                    "パスワードは8〜32文字の半角英数字と_-のみ使用できます");

            return "settings";
        }

        account.setEmail(email);

        if (password != null
                && !password.isBlank()) {

            account.setPassword(
                    passwordService.encode(password));
        }

        accountRepository.save(account);

        session.setAttribute(
                "loginUser",
                account.getEmail());

        model.addAttribute(
                "account",
                account);

        model.addAttribute("emailInput", account.getEmail());
        model.addAttribute("passwordInput", "");

        model.addAttribute(
                "successMessage",
                "設定を更新しました");

        return "settings";
    }

    private Account getLoginAccount(
            HttpSession session) {

        String email =
                (String) session.getAttribute("loginUser");

        return accountRepository.findByEmail(email)
                .orElse(null);
    }
}
