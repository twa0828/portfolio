package com.example.demo.controller;

import java.util.regex.Pattern;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SettingController {

    @GetMapping("/settings")
    public String settings(
            HttpSession session) {

        // 未ログインチェック
        if (session.getAttribute("loginUser") == null) {

            return "redirect:/login";
        }

        return "settings";
    }

    @PostMapping("/settings/update")
    public String updateSettings(

            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String name,

            HttpSession session,
            Model model) {

        // 未ログインチェック
        if (session.getAttribute("loginUser") == null) {

            return "redirect:/login";
        }

        // メールチェック
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

        // パスワードチェック
        if (!password.matches(
                "^[a-zA-Z0-9_-]{8,32}$")) {

            model.addAttribute(
                    "errorMessage",
                    "パスワードは8〜32文字の半角英数字と_-のみ使用できます");

            return "settings";
        }

        // 名前チェック
        if (name.length() > 255) {

            model.addAttribute(
                    "errorMessage",
                    "名前は255文字以内で入力してください");

            return "settings";
        }

        // 成功メッセージ
        model.addAttribute(
                "successMessage",
                "設定を更新しました");

        return "settings";
    }
}