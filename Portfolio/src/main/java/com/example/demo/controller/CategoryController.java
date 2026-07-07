package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(
            CategoryRepository categoryRepository) {

        this.categoryRepository = categoryRepository;
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

        categoryRepository.save(new Category(
                name));

        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        Category category =
                categoryRepository.findById(id)
                        .orElse(null);

        if (category == null) {

            return "redirect:/categories";
        }

        if ("true".equals(category.getDeleted())) {

            return "redirect:/categories";
        }

        model.addAttribute(
                "category",
                category);

        return "category-edit";
    }

    @PostMapping("/categories/update/{id}")
    public String updateCategory(
            @PathVariable Long id,
            @RequestParam String name,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        if (name.length() > 255) {

            Category category =
                    new Category(
                            name);

            category.setId(id);

            model.addAttribute(
                    "errorMessage",
                    "名前は255文字以内です");

            model.addAttribute(
                    "category",
                    category);

            return "category-edit";
        }

        Category category =
                categoryRepository.findById(id)
                        .orElse(null);

        if (category == null) {

            return "redirect:/categories";
        }

        if ("true".equals(category.getDeleted())) {

            return "redirect:/categories";
        }

        category.setName(name);
        categoryRepository.save(category);

        return "redirect:/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        Category category =
                categoryRepository.findById(id)
                        .orElse(null);

        if (category != null) {

            category.setDeleted("true");
            category.setDeletedAt(LocalDateTime.now());
            categoryRepository.save(category);
        }

        return "redirect:/categories";
    }
}
