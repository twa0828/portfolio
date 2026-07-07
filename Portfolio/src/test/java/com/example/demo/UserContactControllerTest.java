package com.example.demo;

import com.example.demo.model.Category;
import com.example.demo.model.Contact;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ContactRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class UserContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {

        contactRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void sendContactSavesContactWithDefaultStatus()
            throws Exception {

        categoryRepository.save(new Category(
                "質問"));

        mockMvc.perform(post("/contact/send")
                        .sessionAttr("loginUser", "user@test.com")
                        .sessionAttr("role", "USER")
                        .param("category", "質問")
                        .param("content", "お問い合わせ内容です"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact-form"))
                .andExpect(model().attribute(
                        "successMessage",
                        "お問い合わせを保存しました"));

        assertThat(contactRepository.findAll())
                .hasSize(1);

        Contact contact =
                contactRepository.findAll().get(0);

        assertThat(contact.getCategory())
                .isEqualTo("質問");

        assertThat(contact.getContent())
                .isEqualTo("お問い合わせ内容です");

        assertThat(contact.getStatus())
                .isEqualTo("未対応");

        assertThat(contact.getDeleted())
                .isEqualTo("false");
    }

    @Test
    void sendContactRedirectsWhenNotLoggedIn()
            throws Exception {

        mockMvc.perform(post("/contact/send")
                        .param("category", "質問")
                        .param("content", "お問い合わせ内容です"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertThat(contactRepository.findAll())
                .isEmpty();
    }

    @Test
    void sendContactDoesNotSaveWhenCategoryIsInvalid()
            throws Exception {

        categoryRepository.save(new Category(
                "質問"));

        mockMvc.perform(post("/contact/send")
                        .sessionAttr("loginUser", "user@test.com")
                        .sessionAttr("role", "USER")
                        .param("category", "存在しないカテゴリー")
                        .param("content", "お問い合わせ内容です"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact-form"))
                .andExpect(model().attribute(
                        "errorMessage",
                        "カテゴリーが不正です"));

        assertThat(contactRepository.findAll())
                .isEmpty();
    }
}
