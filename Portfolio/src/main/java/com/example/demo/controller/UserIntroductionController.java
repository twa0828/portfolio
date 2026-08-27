package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserIntroductionController {

    private final AccountRepository accountRepository;

    public UserIntroductionController(
            AccountRepository accountRepository) {

        this.accountRepository = accountRepository;
    }

    private boolean isUser(
            HttpSession session) {

        return session.getAttribute("loginUser") != null
                && "USER".equals(session.getAttribute("role"));
    }

    @GetMapping("/introductions")
    public String introductions(
            HttpSession session,
            Model model) {

        model.addAttribute(
                "userList",
                getPublicUserList(session));

        model.addAttribute(
                "loggedInUser",
                isUser(session));

        return "introductions";
    }

    @GetMapping("/like-ranking")
    public String likeRanking(
            HttpSession session,
            Model model) {

        List<Account> userList =
                getPublicUserList(session);

        userList.sort(
                Comparator.comparing(
                        Account::getMonthlyLikes)
                        .reversed());

        model.addAttribute(
                "userList",
                userList);

        model.addAttribute(
                "loggedInUser",
                isUser(session));

        return "like-ranking";
    }

    @GetMapping("/introductions/{id}")
    public String detail(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        Account account =
                accountRepository.findById(id)
                        .orElse(null);

        if (!isPublicUser(account, session)) {

            return "redirect:/introductions";
        }

        model.addAttribute(
                "account",
                account);

        model.addAttribute(
                "loggedInUser",
                isUser(session));

        return "introduction-detail";
    }

    @GetMapping("/introductions/image/{id}")
    public ResponseEntity<byte[]> introductionImage(
            @PathVariable Long id,
            HttpSession session) {

        Account account =
                accountRepository.findById(id)
                        .orElse(null);

        if (!isPublicUser(account, session)
                || account.getProfileImage() == null) {

            return ResponseEntity.notFound()
                    .build();
        }

        String contentType =
                account.getProfileImageContentType();

        if (contentType == null
                || contentType.isBlank()) {

            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(account.getProfileImage());
    }

    @PostMapping("/introductions/like/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> like(
            @PathVariable Long id,
            HttpSession session) {

        Account account =
                accountRepository.findById(id)
                        .orElse(null);

        if (!isPublicUser(account, session)) {

            return ResponseEntity.notFound()
                    .build();
        }

        account.setAnnualLikes(
                account.getAnnualLikes() + 1);

        account.setMonthlyLikes(
                account.getMonthlyLikes() + 1);

        accountRepository.save(account);

        Map<String, Integer> result =
                new HashMap<>();

        result.put(
                "annualLikes",
                account.getAnnualLikes());

        result.put(
                "monthlyLikes",
                account.getMonthlyLikes());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/introductions/like-page/{id}")
    public String likeFromPage(
            @PathVariable Long id,
            @RequestParam(defaultValue = "introductions") String returnTo,
            HttpSession session) {

        Account account =
                accountRepository.findById(id)
                        .orElse(null);

        if (isPublicUser(account, session)) {

            account.setAnnualLikes(
                    account.getAnnualLikes() + 1);

            account.setMonthlyLikes(
                    account.getMonthlyLikes() + 1);

            accountRepository.save(account);
        }

        if ("ranking".equals(returnTo)) {
            return "redirect:/like-ranking";
        }

        if ("detail".equals(returnTo)) {
            return "redirect:/introductions/" + id;
        }

        return "redirect:/introductions";
    }

    private List<Account> getPublicUserList(
            HttpSession session) {

        List<Account> userList =
                new ArrayList<>();

        for (Account account : accountRepository.findAll()) {

            if (!isPublicUser(account, session)) {

                continue;
            }

            userList.add(account);
        }

        return userList;
    }

    private boolean isPublicUser(
            Account account,
            HttpSession session) {

        if (account == null) {

            return false;
        }

        String loginEmail =
                (String) session.getAttribute("loginUser");

        return "USER".equals(account.getRole())
                && "false".equals(account.getDeleted())
                && "アクセス許可".equals(account.getStatus())
                && !account.getEmail().equals(loginEmail);
    }
}
