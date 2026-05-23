package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactController {

    @GetMapping("/contacts")
    public String contacts(
            HttpSession session,
            Model model) {

        // 未ログインチェック
        if (session.getAttribute("loginUser") == null) {

            return "redirect:/login";
        }

        List<Map<String, String>> contactList =
                new ArrayList<>();

        contactList.add(Map.of(
                "category", "不具合",
                "content", "ログインできません...",
                "status", "未対応"));

        contactList.add(Map.of(
                "category", "要望",
                "content", "ランキング機能追加...",
                "status", "対応中"));

        contactList.add(Map.of(
                "category", "質問",
                "content", "退会方法について...",
                "status", "対応済み"));

        model.addAttribute(
                "contactList",
                contactList);

        return "contacts";
    }
}
