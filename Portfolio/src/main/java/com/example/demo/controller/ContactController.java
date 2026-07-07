package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.Contact;
import com.example.demo.repository.ContactRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ContactController {

    private final ContactRepository contactRepository;

    public ContactController(
            ContactRepository contactRepository) {

        this.contactRepository = contactRepository;
    }

    private boolean isAdmin(
            HttpSession session) {

        return session.getAttribute("loginUser") != null
                && "ADMIN".equals(session.getAttribute("role"));
    }

    @GetMapping("/contacts")
    public String contacts(
            @RequestParam(
                    defaultValue = "all")
            String status,

            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        List<Contact> viewList =
                new ArrayList<>();

        for (Contact contact : contactRepository.findAll()) {

            if ("true".equals(contact.getDeleted())) {

                continue;
            }

            if (!status.equals("all")
                    && !contact.getStatus().equals(status)) {

                continue;
            }

            viewList.add(contact);
        }

        model.addAttribute(
                "contactList",
                viewList);

        model.addAttribute(
                "selectedStatus",
                status);

        return "contacts";
    }

    @GetMapping("/contacts/{id}")
    public String contactDetail(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        Contact contact =
                contactRepository.findById(id)
                        .orElse(null);

        if (contact == null) {

            return "redirect:/contacts";
        }

        if ("true".equals(contact.getDeleted())) {

            return "redirect:/contacts";
        }

        model.addAttribute(
                "contact",
                contact);

        return "contact-detail";
    }

    @PostMapping("/contacts/status/{id}")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            HttpSession session) {

        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        if (!status.equals("未対応")
                && !status.equals("対応中")
                && !status.equals("対応済み")) {

            return "redirect:/contacts/" + id;
        }

        contactRepository.findById(id)
                .ifPresent(contact -> {
                    contact.setStatus(status);
                    contactRepository.save(contact);
                });

        return "redirect:/contacts/" + id;
    }
}
