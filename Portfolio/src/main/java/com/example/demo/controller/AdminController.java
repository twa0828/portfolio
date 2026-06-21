package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private boolean isAdmin(
            HttpSession session) {

        return session.getAttribute("loginUser") != null
                && "ADMIN".equals(session.getAttribute("role"));
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
	    List<String> yearRankingList =
	            new ArrayList<>();

	    yearRankingList.add("田中 : 520いいね");
	    yearRankingList.add("佐藤 : 430いいね");
	    yearRankingList.add("鈴木 : 390いいね");

	    // 月間ランキング
	    List<String> monthRankingList =
	            new ArrayList<>();

	    monthRankingList.add("山田 : 52いいね");
	    monthRankingList.add("高橋 : 41いいね");
	    monthRankingList.add("伊藤 : 38いいね");

	    model.addAttribute(
	            "yearRankingList",
	            yearRankingList);

	    model.addAttribute(
	            "monthRankingList",
	            monthRankingList);

	    return "admin";
	}

}
