package com.example.demo.controller;

import java.io.IOException;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProfileController {

    private final AccountRepository accountRepository;

    public ProfileController(
            AccountRepository accountRepository) {

        this.accountRepository = accountRepository;
    }

    private boolean isUser(
            HttpSession session) {

        return session.getAttribute("loginUser") != null
                && "USER".equals(session.getAttribute("role"));
    }

    @GetMapping("/profile")
    public String profile(
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

        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(

            @RequestParam String name,

            @RequestParam(required = false)
            MultipartFile profileImage,

            @RequestParam String furigana,
            @RequestParam String gender,
            @RequestParam String age,

            @RequestParam(required = false)
            String profile,

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

        if (name.length() > 255) {

            model.addAttribute(
                    "errorMessage",
                    "名前は255文字以内です");

            return "profile";
        }

        if (profileImage != null
                && profileImage.getSize() > 2097152) {

            model.addAttribute(
                    "errorMessage",
                    "プロフィール画像は2MB以内です");

            return "profile";
        }

        if (furigana.length() > 255) {

            model.addAttribute(
                    "errorMessage",
                    "ふりがなは255文字以内です");

            return "profile";
        }

        if (!furigana.matches(
                "^[ぁ-んー]*$")) {

            model.addAttribute(
                    "errorMessage",
                    "ふりがなはひらがなのみです");

            return "profile";
        }

        if (!gender.equals("男性")
                && !gender.equals("女性")) {

            model.addAttribute(
                    "errorMessage",
                    "性別が不正です");

            return "profile";
        }

        if (!age.matches(
                "^[0-9]{1,3}$")) {

            model.addAttribute(
                    "errorMessage",
                    "年齢は3桁以内の数字です");

            return "profile";
        }

        if (profile == null) {

            profile = "";
        }

        if (profile.length() > 1500) {

            model.addAttribute(
                    "errorMessage",
                    "自己紹介は1500文字以内です");

            return "profile";
        }

        account.setName(name);
        account.setFurigana(furigana);
        account.setGender(gender);
        account.setAge(age);
        account.setProfile(profile);

        if (profileImage != null
                && !profileImage.isEmpty()) {

            try {

                account.setProfileImage(
                        profileImage.getBytes());

                account.setProfileImageContentType(
                        profileImage.getContentType());

            } catch (IOException e) {

                model.addAttribute(
                        "errorMessage",
                        "プロフィール画像の保存に失敗しました");

                return "profile";
            }
        }

        accountRepository.save(account);

        model.addAttribute(
                "account",
                account);

        model.addAttribute(
                "successMessage",
                "プロフィールを更新しました");

        return "profile";
    }

    @GetMapping("/profile/image")
    public ResponseEntity<byte[]> profileImage(
            HttpSession session) {

        if (!isUser(session)) {

            return ResponseEntity.notFound()
                    .build();
        }

        Account account =
                getLoginAccount(session);

        if (account == null
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

    private Account getLoginAccount(
            HttpSession session) {

        String email =
                (String) session.getAttribute("loginUser");

        return accountRepository.findByEmail(email)
                .orElse(null);
    }
}
