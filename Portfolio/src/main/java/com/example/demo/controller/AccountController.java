package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    // 仮DB
    private List<Map<String, String>> accountList =
            new ArrayList<>();

    // コンストラクタ
    public AccountController() {

        accountList.add(Map.of(
                "id", "1",
                "name", "田中",
                "email", "tanaka@test.com",
                "status", "アクセス許可"
        ));

        accountList.add(Map.of(
                "id", "2",
                "name", "佐藤",
                "email", "sato@test.com",
                "status", "アクセス禁止"
        ));

        accountList.add(Map.of(
                "id", "3",
                "name", "鈴木",
                "email", "suzuki@test.com",
                "status", "アクセス許可"
        ));
    }

    @GetMapping("/accounts")
    public String accounts(
            HttpSession session,
            Model model) {

        // 未ログインチェック
        if (session.getAttribute("loginUser") == null) {

            return "redirect:/login";
        }

        model.addAttribute(
                "accountList",
                accountList);

        return "accounts";
    }

    @PostMapping("/accounts/delete/{id}")
    public String deleteAccount(
            @PathVariable String id) {

        accountList.removeIf(
                account ->
                account.get("id").equals(id));

        return "redirect:/accounts";
    }
    @GetMapping("/accounts/edit/{id}")
    public String editAccount(
            @PathVariable String id,
            HttpSession session,
            Model model) {

        // 未ログインチェック
        if (session.getAttribute("loginUser") == null) {

            return "redirect:/login";
        }

        // id一致検索
        for (Map<String, String> account : accountList) {

            if (account.get("id").equals(id)) {

                model.addAttribute(
                        "account",
                        account);

                break;
            }
        }

        return "account-edit";
    }@PostMapping("/accounts/update/{id}")
    public String updateAccount(
            @PathVariable String id,

            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String status) {

        for (int i = 0; i < accountList.size(); i++) {

            Map<String, String> account =
                    accountList.get(i);

            if (account.get("id").equals(id)) {

                accountList.set(i,
                        Map.of(
                            "id", id,
                            "name", name,
                            "email", email,
                            "status", status
                        ));

                break;
            }
        }

        return "redirect:/accounts";
    }
    @PostMapping("/accounts/status/{id}")
    public String changeStatus(
            @PathVariable String id) {

        for (int i = 0; i < accountList.size(); i++) {

            Map<String, String> account =
                    accountList.get(i);

            if (account.get("id").equals(id)) {

                String newStatus;

                // 切替
                if (account.get("status")
                        .equals("アクセス許可")) {

                    newStatus = "アクセス禁止";

                } else {

                    newStatus = "アクセス許可";
                }

                // 更新
                accountList.set(i,
                        Map.of(
                            "id", account.get("id"),
                            "name", account.get("name"),
                            "email", account.get("email"),
                            "status", newStatus
                        ));

                break;
            }
        }

        return "redirect:/accounts";
    }
    
}