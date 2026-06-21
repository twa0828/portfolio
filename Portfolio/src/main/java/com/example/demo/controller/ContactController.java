package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import com.example.demo.model.Contact;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ContactController {

    // 仮DB
    private List<Contact> contactList =
            new ArrayList<>();

    private boolean isAdmin(
            HttpSession session) {

        return session.getAttribute("loginUser") != null
                && "ADMIN".equals(session.getAttribute("role"));
    }

    // コンストラクタ
    public ContactController() {

        contactList.add(new Contact(
                "1",
                "不具合",
                "ログインできません。\nメールアドレスとパスワードを入力してもログイン画面に戻ってしまいます。",
                "未対応"));

        contactList.add(new Contact(
                "2",
                "要望",
                "ランキング機能を追加してほしいです。\n年間ランキングだけでなく、週間ランキングも見たいです。",
                "対応中"));

        contactList.add(new Contact(
                "3",
                "質問",
                "退会方法について教えてください。\nマイページから操作できますか？",
                "対応済み"));
    }

    @GetMapping("/contacts")
    public String contacts(
            @RequestParam(
                    defaultValue = "all")
            String status,

            HttpSession session,
            Model model) {

        // 管理者チェック
        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        List<Contact> viewList =
                new ArrayList<>();

        for (Contact contact : contactList) {

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
            @PathVariable String id,
            HttpSession session,
            Model model) {

        // 管理者チェック
        if (!isAdmin(session)) {

            return "redirect:/login";
        }

        for (Contact contact : contactList) {

            if (contact.getId().equals(id)) {

                model.addAttribute(
                        "contact",
                        contact);

                return "contact-detail";
            }
        }

        return "redirect:/contacts";
    }

    @PostMapping("/contacts/status/{id}")
    public String updateStatus(
            @PathVariable String id,
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

        for (int i = 0; i < contactList.size(); i++) {

            Contact contact =
                    contactList.get(i);

            if (contact.getId().equals(id)) {

                contact.setStatus(status);

                break;
            }
        }

        return "redirect:/contacts/" + id;
    }
}
