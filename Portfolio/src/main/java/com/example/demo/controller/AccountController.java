package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import com.example.demo.model.Account;

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
    private List<Account> accountList =
            new ArrayList<>();

    private boolean isAdmin(
            HttpSession session) {

        return session.getAttribute("loginUser") != null
                && "ADMIN".equals(session.getAttribute("role"));
    }

    // コンストラクタ
    public AccountController() {

        accountList.add(new Account(
                "1",
                "ADMIN",
                "田中",
                "tanaka@test.com",
                "password1",
                "アクセス許可",
                "false"));

        accountList.add(new Account(
                "2",
                "ADMIN",
                "佐藤",
                "sato@test.com",
                "password1",
                "アクセス禁止",
                "false"));

        accountList.add(new Account(
                "3",
                "ADMIN",
                "鈴木",
                "suzuki@test.com",
                "password1",
                "アクセス許可",
                "false"));

        accountList.add(new Account(
                "4",
                "ADMIN",
                "高橋",
                "takahashi@test.com",
                "password1",
                "アクセス許可",
                "false"));

        accountList.add(new Account(
                "5",
                "ADMIN",
                "伊藤",
                "ito@test.com",
                "password1",
                "アクセス禁止",
                "false"));

        accountList.add(new Account(
                "6",
                "ADMIN",
                "渡辺",
                "watanabe@test.com",
                "password1",
                "アクセス許可",
                "false"));

        accountList.add(new Account(
                "7",
                "ADMIN",
                "山本",
                "yamamoto@test.com",
                "password1",
                "アクセス禁止",
                "false"));
        
    }

    @GetMapping("/accounts")
    public String accounts(

            @RequestParam(
                    defaultValue = "1")
            int page,

            HttpSession session,
            Model model) {

        // 管理者チェック
        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        int pageSize = 5;

        // 削除されていないアカウントだけ取得
        List<Account> activeList =
                new ArrayList<>();

        for (Account account : accountList) {

            if (account.getDeleted()
                    .equals("false")) {

                activeList.add(account);
            }
            
        }

        // 総ページ数
        int totalPages =
                (int)Math.ceil(
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

        // 開始位置
        int start =
                (page - 1) * pageSize;

        // 終了位置
        int end =
                Math.min(
                        start + pageSize,
                        activeList.size());

        // 現在ページ分だけ取得
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
            HttpSession session){

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

        // 基本項目チェック
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

        for (Account account : accountList) {

            if (account.getEmail()
                    .equals(email)) {

                model.addAttribute(
                        "errorMessage",
                        "このメールアドレスは既に登録されています");

                return "account-create";
            }
        }
 
        if (role.equals("USER")) {

    	    // ふりがな
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

    	    // 性別
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

    	    // 年齢
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

    	    // 自己紹介
            if (profile == null) {

                profile = "";
            }

    	    if (profile.length() > 1500) {

    	        model.addAttribute(
    	                "errorMessage",
    	                "自己紹介は1500文字以内です");

    	        return "account-create";
    	    }

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

    	    Account account =
    	            new Account(
    	                    newId,
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

    	    accountList.add(account);

    	    return "redirect:/accounts";
    	}
    
    

    @PostMapping("/accounts/delete/{id}")
    public String deleteAccount(
            @PathVariable String id,
            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        for (int i = 0;
                i < accountList.size();
                i++) {

            Account account =
                    accountList.get(i);

            if (account.getId()
                    .equals(id)) {

                account.setDeleted("true");

                break;
            }
        }

        return "redirect:/accounts";
    }
    @GetMapping("/accounts/edit/{id}")
    public String editAccount(
            @PathVariable String id,
            HttpSession session,
            Model model) {

        // 管理者チェック
        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        // id一致検索
        for (Account account : accountList) {

            if (account.getId().equals(id)) {

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
            @RequestParam String status,

            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        for (int i = 0; i < accountList.size(); i++) {

            Account account =
                    accountList.get(i);

            if (account.getId().equals(id)) {

            	account.setName(name);
            	account.setEmail(email);
            	account.setStatus(status);

                break;
            }
        }

        return "redirect:/accounts";
    }
    @PostMapping("/accounts/status/{id}")
    public String changeStatus(
            @PathVariable String id,
            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        for (int i = 0; i < accountList.size(); i++) {

            Account account =
                    accountList.get(i);

            if (account.getId().equals(id)) {

                String newStatus;

                // 切替
                if (account.getStatus()
                        .equals("アクセス許可")) {

                    newStatus = "アクセス禁止";

                } else {

                    newStatus = "アクセス許可";
                }

                // 更新
                account.setStatus(newStatus);

                break;
            }
        }

        return "redirect:/accounts";
    }
 
    @GetMapping("/accounts/deleted")
    public String deletedAccounts(
    		HttpSession session,
    		Model model) {

		// 管理者チェック
		if (!isAdmin(session)) {

			return "redirect:/login";
		}

		List<Account> deletedList =
				new ArrayList<>();

		for (Account account : accountList) {

			if (account.getDeleted()
					.equals("true")) {

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
            @PathVariable String id,
            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        for (int i = 0; i < accountList.size(); i++) {

            Account account =
                    accountList.get(i);

            if (account.getId().equals(id)) {

                account.setDeleted("false");

                break;
            }
        }

        return "redirect:/accounts/deleted";
    }
    @PostMapping("/accounts/permanent-delete/{id}")
    public String permanentDeleteAccount(
            @PathVariable String id,
            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        accountList.removeIf(
                account ->
                account.getId().equals(id));

        return "redirect:/accounts/deleted";
    }
}
