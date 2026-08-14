package com.example.demo;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;

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
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {

        accountRepository.deleteAll();
    }

    @Test
    void adminLoginSuccessRedirectsToAdminDashboard()
            throws Exception {

        accountRepository.save(new Account(
                "ADMIN",
                "管理者",
                "admin@test.com",
                "password1",
                "アクセス許可",
                "false"));

        mockMvc.perform(post("/login")
                        .param("userId", "admin@test.com")
                        .param("password", "password1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        Account account =
                accountRepository.findByEmail("admin@test.com")
                        .orElseThrow();

        assertThat(account.getPassword())
                .isNotEqualTo("password1");

        assertThat(account.getPassword())
                .startsWith("$2");
    }

    @Test
    void blankUserIdShowsErrorMessage()
            throws Exception {

        mockMvc.perform(post("/login")
                        .param("userId", "")
                        .param("password", "password1"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute(
                        "errorMessage",
                        "メールアドレスを入力してください"));
    }

    @Test
    void invalidPasswordFormatShowsErrorMessage()
            throws Exception {

        mockMvc.perform(post("/login")
                        .param("userId", "user@test.com")
                        .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute(
                        "errorMessage",
                        "パスワードは8〜32文字の半角英数字と_-のみ使用できます"));
    }
}
