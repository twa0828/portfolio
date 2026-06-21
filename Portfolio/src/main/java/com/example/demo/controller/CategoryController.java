package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import com.example.demo.model.Category;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CategoryController {

    // 仮DB
    private List<Category> categoryList =
            new ArrayList<>();

    public CategoryController() {

        categoryList.add(new Category(
                "1",
                "不具合"));

        categoryList.add(new Category(
                "2",
                "要望"));

        categoryList.add(new Category(
                "3",
                "質問"));
    }

    private boolean isAdmin(
            HttpSession session) {

        return session.getAttribute("loginUser") != null
                && "ADMIN".equals(session.getAttribute("role"));
    }

    @GetMapping("/categories")
    public String categories(
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        model.addAttribute(
                "categoryList",
                categoryList);

        return "categories";
    }

    @GetMapping("/categories/create")
    public String createForm(
            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        return "category-create";
    }

    @PostMapping("/categories/create")
    public String createCategory(
            @RequestParam String name,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        if (name.length() > 255) {

            model.addAttribute(
                    "errorMessage",
                    "名前は255文字以内です");

            return "category-create";
        }

        String newId =
                String.valueOf(
                        categoryList.size() + 1);

        categoryList.add(new Category(
                newId,
                name));

        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(
            @PathVariable String id,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        for (Category category : categoryList) {

            if (category.getId().equals(id)) {

                model.addAttribute(
                        "category",
                        category);

                return "category-edit";
            }
        }

        return "redirect:/categories";
    }

    @PostMapping("/categories/update/{id}")
    public String updateCategory(
            @PathVariable String id,
            @RequestParam String name,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        if (name.length() > 255) {

            model.addAttribute(
                    "errorMessage",
                    "名前は255文字以内です");

            model.addAttribute(
                    "category",
                    new Category(
                            id,
                            name));

            return "category-edit";
        }

        for (Category category : categoryList) {

            if (category.getId().equals(id)) {

                category.setName(name);

                break;
            }
        }

        return "redirect:/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(
            @PathVariable String id,
            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        categoryList.removeIf(
                category ->
                category.getId().equals(id));

        return "redirect:/categories";
    }
}
