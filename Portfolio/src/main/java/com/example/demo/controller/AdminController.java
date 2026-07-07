package com.example.demo.controller;

import java.util.Comparator;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final AccountRepository accountRepository;

    public AdminController(
            AccountRepository accountRepository) {

        this.accountRepository = accountRepository;
    }

    private boolean isAdmin(
            HttpSession session) {

        return session.getAttribute("loginUser") != null
                && "ADMIN".equals(session.getAttribute("role"));
    }

    private boolean isRankingTarget(
            Account account) {

        return "USER".equals(account.getRole())
                && !"true".equals(account.getDeleted())
                && "アクセス許可".equals(account.getStatus());
    }

	@GetMapping("/admin")
	public String admin(
	        HttpSession session,
	        Model model) {

	    // 管理者チェック
	    if (!isAdmin(session)) {

	        return "redirect:/login";
	    }

	    // 年間ランキング
	    List<Account> yearRankingList =
	            accountRepository.findAll()
	                    .stream()
	                    .filter(this::isRankingTarget)
	                    .sorted(Comparator.comparing(
	                            Account::getAnnualLikes)
	                            .reversed())
	                    .toList();

	    // 月間ランキング
	    List<Account> monthRankingList =
	            accountRepository.findAll()
	                    .stream()
	                    .filter(this::isRankingTarget)
	                    .sorted(Comparator.comparing(
	                            Account::getMonthlyLikes)
	                            .reversed())
	                    .toList();

	    model.addAttribute(
	            "yearRankingList",
	            yearRankingList);

	    model.addAttribute(
	            "monthRankingList",
	            monthRankingList);

	    return "admin";
	}

}
