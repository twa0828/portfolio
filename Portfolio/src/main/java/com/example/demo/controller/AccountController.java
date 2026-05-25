package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        accountList.add(Map.of(
                "id", "4",
                "name", "高橋",
                "email", "takahashi@test.com",
                "status", "アクセス許可"
        ));

        accountList.add(Map.of(
                "id", "5",
                "name", "伊藤",
                "email", "ito@test.com",
                "status", "アクセス禁止"
        ));

        accountList.add(Map.of(
                "id", "6",
                "name", "渡辺",
                "email", "watanabe@test.com",
                "status", "アクセス許可"
        ));

        accountList.add(Map.of(
                "id", "7",
                "name", "山本",
                "email", "yamamoto@test.com",
                "status", "アクセス禁止"
        ));
        
    }

    @GetMapping("/accounts")
    public String accounts(

            @RequestParam(
                    defaultValue = "1")
            int page,

            HttpSession session,
            Model model) {

        // 未ログインチェック
        if (session.getAttribute("loginUser") == null) {

            return "redirect:/login";
        }

        int pageSize = 5;

        // 開始位置
        int start =
                (page - 1) * pageSize;

        // 終了位置
        int end =
                Math.min(
                        start + pageSize,
                        accountList.size());

        // 現在ページ分だけ取得
        List<Map<String, String>> pageList =
                accountList.subList(start, end);

        // 総ページ数
        int totalPages =
                (int)Math.ceil(
                        (double) accountList.size()
                        / pageSize);

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
            HttpSession session){

        if (session.getAttribute("loginUser") == null) {

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

            Model model) {
    	// 名前チェック
    	if (name.length() > 255) {

    	    model.addAttribute(
    	            "errorMessage",
    	            "名前は255文字以内です");

    	    return "account-create";
    	}

    	// メールチェック
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

    	// パスワードチェック
    	if (!password.matches(
    	        "^[a-zA-Z0-9_-]{8,32}$")) {

    	    model.addAttribute(
    	            "errorMessage",
    	            "パスワードは8〜32文字の半角英数字と_-のみです");

    	    return "account-create";
    	}
 

    	    // ふりがな
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

    	    // 性別
    	    if (!gender.equals("男性")
    	            && !gender.equals("女性")) {

    	        model.addAttribute(
    	                "errorMessage",
    	                "性別が不正です");

    	        return "account-create";
    	    }

    	    // 年齢
    	    if (!age.matches(
    	            "^[0-9]{1,3}$")) {

    	        model.addAttribute(
    	                "errorMessage",
    	                "年齢は3桁以内の数字です");

    	        return "account-create";
    	    }

    	    // 自己紹介
    	    if (profile.length() > 1500) {

    	        model.addAttribute(
    	                "errorMessage",
    	                "自己紹介は1500文字以内です");

    	        return "account-create";
    	    }
    	   	if (role.equals("USER")) {

        	    // 画像サイズ
        	    if (profileImage != null
        	            && profileImage.getSize() > 2097152) {

        	        model.addAttribute(
        	                "errorMessage",
        	                "画像は2MB以内です");

        	    	return "account-create";
        	    }
        	    		    }
    	    String newId =
    	            String.valueOf(
    	                    accountList.size() + 1);

    	    Map<String, String> account =
    	            new HashMap<>();

    	    account.put("id", newId);
    	    account.put("role", role);
    	    account.put("name", name);
    	    account.put("email", email);
    	    account.put("password", password);
    	    account.put("status", status);

    	    if (role.equals("USER")) {

    	        account.put("furigana", furigana);
    	        account.put("gender", gender);
    	        account.put("age", age);
    	        account.put("profile", profile);
    	    }

    	    accountList.add(account);

    	    return "redirect:/accounts";
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