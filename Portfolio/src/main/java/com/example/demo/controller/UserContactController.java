package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.Category;
import com.example.demo.model.Contact;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ContactRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserContactController {

    private final ContactRepository contactRepository;
    private final CategoryRepository categoryRepository;

    public UserContactController(
            ContactRepository contactRepository,
            CategoryRepository categoryRepository) {

        this.contactRepository = contactRepository;
        this.categoryRepository = categoryRepository;
    }

    private boolean isUser(
            HttpSession session) {

        return session.getAttribute("loginUser") != null
                && "USER".equals(session.getAttribute("role"));
    }

    @GetMapping("/contact")
    public String contactForm(
            HttpSession session,
            Model model) {

        if (!isUser(session)) {

            return "redirect:/login";
        }

        setCategoryList(model);

        return "contact-form";
    }

    @PostMapping("/contact/send")
    public String sendContact(

            @RequestParam String category,
            @RequestParam String content,

            HttpSession session,
            Model model) {

        if (!isUser(session)) {

            return "redirect:/login";
        }

        setCategoryList(model);

        if (category == null
                || category.isBlank()) {

            model.addAttribute(
                    "errorMessage",
                    "カテゴリーを選択してください");

            return "contact-form";
        }

        if (!existsCategory(category)) {

            model.addAttribute(
                    "errorMessage",
                    "カテゴリーが不正です");

            return "contact-form";
        }

        if (content == null
                || content.isBlank()) {

            model.addAttribute(
                    "errorMessage",
                    "お問い合わせ内容を入力してください");

            return "contact-form";
        }

        if (content.length() > 1500) {

            model.addAttribute(
                    "errorMessage",
                    "お問い合わせ内容は1500文字以内です");

            return "contact-form";
        }

        contactRepository.save(new Contact(
                category,
                content,
                "未対応"));

        model.addAttribute(
                "successMessage",
                "お問い合わせを保存しました");

        return "contact-form";
    }

    private void setCategoryList(
            Model model) {

        List<Category> categoryList =
                new ArrayList<>();

        for (Category category : categoryRepository.findAll()) {

            if (!"true".equals(category.getDeleted())) {

                categoryList.add(category);
            }
        }

        model.addAttribute(
                "categoryList",
                categoryList);
    }

    private boolean existsCategory(
            String category) {

        for (Category registeredCategory : categoryRepository.findAll()) {

            if ("true".equals(registeredCategory.getDeleted())) {

                continue;
            }

            if (registeredCategory.getName().equals(category)) {

                return true;
            }
        }

        return false;
    }
}
