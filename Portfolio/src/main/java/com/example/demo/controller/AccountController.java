package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController(
            AccountRepository accountRepository) {

        this.accountRepository = accountRepository;
    }

    private boolean isAdmin(
            HttpSession session) {

        return session.getAttribute("loginUser") != null
                && "ADMIN".equals(session.getAttribute("role"));
    }

    @GetMapping("/accounts")
    public String accounts(

            @RequestParam(
                    defaultValue = "1")
            int page,

            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        int pageSize = 5;

        List<Account> activeList =
                new ArrayList<>();

        for (Account account : accountRepository.findAll()) {

            if ("false".equals(account.getDeleted())) {

                activeList.add(account);
            }
        }

        int totalPages =
                (int) Math.ceil(
                        (double) activeList.size()
                                / pageSize);

        if (totalPages == 0) {

            totalPages = 1;
        }

        if (page < 1) {

            page = 1;
        }

        if (page > totalPages) {

            page = totalPages;
        }

        int start =
                (page - 1) * pageSize;

        int end =
                Math.min(
                        start + pageSize,
                        activeList.size());

        List<Account> pageList =
                activeList.subList(start, end);

        model.addAttribute(
                "accountList",
                pageList);

        model.addAttribute(
                "currentPage",
                page);

        model.addAttribute(
                "totalPages",
                totalPages);

        return "accounts";
    }

    @GetMapping("/accounts/create")
    public String createForm(
            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        return "account-create";
    }

    @PostMapping("/accounts/create")
    public String createAccount(

            @RequestParam String role,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String status,

            @RequestParam(required = false)
            MultipartFile profileImage,

            @RequestParam(required = false)
            String furigana,

            @RequestParam(required = false)
            String gender,

            @RequestParam(required = false)
            String age,

            @RequestParam(required = false)
            String profile,

            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        if (name == null
                || name.isBlank()) {

            model.addAttribute(
                    "errorMessage",
                    "名前を入力してください");

            return "account-create";
        }

        if (email == null
                || email.isBlank()) {

            model.addAttribute(
                    "errorMessage",
                    "メールアドレスを入力してください");

            return "account-create";
        }

        if (password == null
                || password.isBlank()) {

            model.addAttribute(
                    "errorMessage",
                    "パスワードを入力してください");

            return "account-create";
        }

        if (role == null
                || (!role.equals("ADMIN")
                && !role.equals("USER"))) {

            model.addAttribute(
                    "errorMessage",
                    "権限が不正です");

            return "account-create";
        }

        if (status == null
                || (!status.equals("アクセス許可")
                && !status.equals("アクセス禁止"))) {

            model.addAttribute(
                    "errorMessage",
                    "ステータスが不正です");

            return "account-create";
        }

        if (name.length() > 255) {

            model.addAttribute(
                    "errorMessage",
                    "名前は255文字以内です");

            return "account-create";
        }

        if (email.length() > 255) {

            model.addAttribute(
                    "errorMessage",
                    "メールアドレスは255文字以内です");

            return "account-create";
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            model.addAttribute(
                    "errorMessage",
                    "メール形式が正しくありません");

            return "account-create";
        }

        if (!password.matches(
                "^[a-zA-Z0-9_-]{8,32}$")) {

            model.addAttribute(
                    "errorMessage",
                    "パスワードは8〜32文字の半角英数字と_-のみです");

            return "account-create";
        }

        for (Account account : accountRepository.findAll()) {

            if (account.getEmail()
                    .equals(email)) {

                model.addAttribute(
                        "errorMessage",
                        "このメールアドレスは既に登録されています");

                return "account-create";
            }
        }

        if (role.equals("USER")) {

            if (furigana == null
                    || furigana.isBlank()) {

                model.addAttribute(
                        "errorMessage",
                        "ふりがなを入力してください");

                return "account-create";
            }

            if (furigana.length() > 255) {

                model.addAttribute(
                        "errorMessage",
                        "ふりがなは255文字以内です");

                return "account-create";
            }

            if (!furigana.matches(
                    "^[ぁ-んー]*$")) {

                model.addAttribute(
                        "errorMessage",
                        "ふりがなはひらがなのみです");

                return "account-create";
            }

            if (gender == null
                    || gender.isBlank()) {

                model.addAttribute(
                        "errorMessage",
                        "性別を選択してください");

                return "account-create";
            }

            if (!gender.equals("男性")
                    && !gender.equals("女性")) {

                model.addAttribute(
                        "errorMessage",
                        "性別が不正です");

                return "account-create";
            }

            if (age == null
                    || age.isBlank()) {

                model.addAttribute(
                        "errorMessage",
                        "年齢を入力してください");

                return "account-create";
            }

            if (!age.matches(
                    "^[0-9]{1,3}$")) {

                model.addAttribute(
                        "errorMessage",
                        "年齢は3桁以内の数字です");

                return "account-create";
            }

            if (profile == null) {

                profile = "";
            }

            if (profile.length() > 1500) {

                model.addAttribute(
                        "errorMessage",
                        "自己紹介は1500文字以内です");

                return "account-create";
            }

            if (profileImage != null
                    && profileImage.getSize() > 2097152) {

                model.addAttribute(
                        "errorMessage",
                        "画像は2MB以内です");

                return "account-create";
            }
        }

        Account account =
                new Account(
                        role,
                        name,
                        email,
                        password,
                        status,
                        "false");

        if (role.equals("USER")) {

            account.setFurigana(furigana);
            account.setGender(gender);
            account.setAge(age);
            account.setProfile(profile);
        }

        accountRepository.save(account);

        return "redirect:/accounts";
    }

    @PostMapping("/accounts/delete/{id}")
    public String deleteAccount(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        accountRepository.findById(id)
                .ifPresent(account -> {
                    account.setDeleted("true");
                    accountRepository.save(account);
                });

        return "redirect:/accounts";
    }

    @GetMapping("/accounts/edit/{id}")
    public String editAccount(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        Account account =
                accountRepository.findById(id)
                        .orElse(null);

        if (account == null) {

            return "redirect:/accounts";
        }

        model.addAttribute(
                "account",
                account);

        return "account-edit";
    }

    @PostMapping("/accounts/update/{id}")
    public String updateAccount(
            @PathVariable Long id,

            @RequestParam String role,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String status,

            @RequestParam(required = false)
            MultipartFile profileImage,

            @RequestParam(required = false)
            String furigana,

            @RequestParam(required = false)
            String gender,

            @RequestParam(required = false)
            String age,

            @RequestParam(required = false)
            String profile,

            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        Account account =
                accountRepository.findById(id)
                        .orElse(null);

        if (account == null) {

            return "redirect:/accounts";
        }

        model.addAttribute(
                "account",
                account);

        if (name == null
                || name.isBlank()) {

            model.addAttribute(
                    "errorMessage",
                    "名前を入力してください");

            return "account-edit";
        }

        if (email == null
                || email.isBlank()) {

            model.addAttribute(
                    "errorMessage",
                    "メールアドレスを入力してください");

            return "account-edit";
        }

        if (role == null
                || (!role.equals("ADMIN")
                && !role.equals("USER"))) {

            model.addAttribute(
                    "errorMessage",
                    "権限が不正です");

            return "account-edit";
        }

        if (status == null
                || (!status.equals("アクセス許可")
                && !status.equals("アクセス禁止"))) {

            model.addAttribute(
                    "errorMessage",
                    "ステータスが不正です");

            return "account-edit";
        }

        if (name.length() > 255) {

            model.addAttribute(
                    "errorMessage",
                    "名前は255文字以内です");

            return "account-edit";
        }

        if (email.length() > 255) {

            model.addAttribute(
                    "errorMessage",
                    "メールアドレスは255文字以内です");

            return "account-edit";
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            model.addAttribute(
                    "errorMessage",
                    "メール形式が正しくありません");

            return "account-edit";
        }

        Account sameEmailAccount =
                accountRepository.findByEmail(email)
                        .orElse(null);

        if (sameEmailAccount != null
                && !sameEmailAccount.getId().equals(account.getId())) {

            model.addAttribute(
                    "errorMessage",
                    "このメールアドレスは既に登録されています");

            return "account-edit";
        }

        if (role.equals("USER")) {

            if (furigana == null
                    || furigana.isBlank()) {

                model.addAttribute(
                        "errorMessage",
                        "ふりがなを入力してください");

                return "account-edit";
            }

            if (furigana.length() > 255) {

                model.addAttribute(
                        "errorMessage",
                        "ふりがなは255文字以内です");

                return "account-edit";
            }

            if (!furigana.matches(
                    "^[ぁ-んー]*$")) {

                model.addAttribute(
                        "errorMessage",
                        "ふりがなはひらがなのみです");

                return "account-edit";
            }

            if (gender == null
                    || gender.isBlank()) {

                model.addAttribute(
                        "errorMessage",
                        "性別を選択してください");

                return "account-edit";
            }

            if (!gender.equals("男性")
                    && !gender.equals("女性")) {

                model.addAttribute(
                        "errorMessage",
                        "性別が不正です");

                return "account-edit";
            }

            if (age == null
                    || age.isBlank()) {

                model.addAttribute(
                        "errorMessage",
                        "年齢を入力してください");

                return "account-edit";
            }

            if (!age.matches(
                    "^[0-9]{1,3}$")) {

                model.addAttribute(
                        "errorMessage",
                        "年齢は3桁以内の数字です");

                return "account-edit";
            }

            if (profile == null) {

                profile = "";
            }

            if (profile.length() > 1500) {

                model.addAttribute(
                        "errorMessage",
                        "自己紹介は1500文字以内です");

                return "account-edit";
            }

            if (profileImage != null
                    && profileImage.getSize() > 2097152) {

                model.addAttribute(
                        "errorMessage",
                        "画像は2MB以内です");

                return "account-edit";
            }
        }

        account.setRole(role);
        account.setName(name);
        account.setEmail(email);
        account.setStatus(status);

        if (role.equals("USER")) {

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
                            "画像の保存に失敗しました");

                    return "account-edit";
                }
            }
        }

        accountRepository.save(account);

        return "redirect:/accounts";
    }

    @PostMapping("/accounts/status/{id}")
    public String changeStatus(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        accountRepository.findById(id)
                .ifPresent(account -> {
                    String newStatus;

                    if (account.getStatus()
                            .equals("アクセス許可")) {

                        newStatus = "アクセス禁止";

                    } else {

                        newStatus = "アクセス許可";
                    }

                    account.setStatus(newStatus);
                    accountRepository.save(account);
                });

        return "redirect:/accounts";
    }

    @GetMapping("/accounts/deleted")
    public String deletedAccounts(
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        List<Account> deletedList =
                new ArrayList<>();

        for (Account account : accountRepository.findAll()) {

            if ("true".equals(account.getDeleted())) {

                deletedList.add(account);
            }
        }

        model.addAttribute(
                "deletedList",
                deletedList);

        return "deleted-accounts";
    }

    @PostMapping("/accounts/restore/{id}")
    public String restoreAccount(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        accountRepository.findById(id)
                .ifPresent(account -> {
                    account.setDeleted("false");
                    accountRepository.save(account);
                });

        return "redirect:/accounts/deleted";
    }

    @PostMapping("/accounts/permanent-delete/{id}")
    public String permanentDeleteAccount(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        accountRepository.deleteById(id);

        return "redirect:/accounts/deleted";
    }
}
