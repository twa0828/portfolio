package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String admin(
            HttpSession session,
            Model model) {

        // 未ログインチェック
        if (session.getAttribute("loginUser") == null) {

            return "redirect:/login";
        }

        // ランキング
        List<String> rankingList =
                new ArrayList<>();

        rankingList.add("田中 : 120いいね");
        rankingList.add("佐藤 : 98いいね");
        rankingList.add("鈴木 : 76いいね");

        model.addAttribute(
                "rankingList",
                rankingList);

        return "admin";
    }
}