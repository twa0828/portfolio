package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final AccountRepository accountRepository;

    public UserController(
            AccountRepository accountRepository) {

        this.accountRepository = accountRepository;
    }

    private boolean isUser(
            HttpSession session) {

        return session.getAttribute("loginUser") != null
                && "USER".equals(session.getAttribute("role"));
    }

    @GetMapping("/user")
    public String user(
            HttpSession session,
            Model model) {

        if (!isUser(session)) {

            return "redirect:/login";
        }

        String email =
                (String) session.getAttribute("loginUser");

        Account account =
                accountRepository.findByEmail(email)
                        .orElse(null);

        if (account == null) {

            return "redirect:/login";
        }

        model.addAttribute(
                "account",
                account);

        return "user";
    }
}
