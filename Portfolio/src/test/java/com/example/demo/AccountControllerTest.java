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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {

        accountRepository.deleteAll();
    }

    @Test
    void createUserAccountSavesAccount()
            throws Exception {

        mockMvc.perform(multipart("/accounts/create")
                        .sessionAttr("loginUser", "admin@test.com")
                        .sessionAttr("role", "ADMIN")
                        .param("role", "USER")
                        .param("name", "一般ユーザー")
                        .param("email", "user@test.com")
                        .param("password", "password1")
                        .param("status", "アクセス許可")
                        .param("furigana", "いっぱんゆーざー")
                        .param("gender", "男性")
                        .param("age", "25")
                        .param("profile", "自己紹介です"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts"));

        Account account =
                accountRepository.findByEmail("user@test.com")
                        .orElseThrow();

        assertThat(account.getRole())
                .isEqualTo("USER");

        assertThat(account.getName())
                .isEqualTo("一般ユーザー");

        assertThat(account.getStatus())
                .isEqualTo("アクセス許可");

        assertThat(account.getDeleted())
                .isEqualTo("false");

        assertThat(account.getFurigana())
                .isEqualTo("いっぱんゆーざー");
    }

    @Test
    void deleteAccountMarksAccountAsDeleted()
            throws Exception {

        Account account =
                accountRepository.save(new Account(
                        "USER",
                        "一般ユーザー",
                        "user@test.com",
                        "password1",
                        "アクセス許可",
                        "false"));

        mockMvc.perform(post("/accounts/delete/{id}", account.getId())
                        .sessionAttr("loginUser", "admin@test.com")
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts"));

        Account deletedAccount =
                accountRepository.findById(account.getId())
                        .orElseThrow();

        assertThat(deletedAccount.getDeleted())
                .isEqualTo("true");

        assertThat(deletedAccount.getDeletedAt())
                .isNotNull();
    }

    @Test
    void restoreAccountClearsDeletedFlag()
            throws Exception {

        Account account =
                accountRepository.save(new Account(
                        "USER",
                        "一般ユーザー",
                        "user@test.com",
                        "password1",
                        "アクセス許可",
                        "true"));

        mockMvc.perform(post("/accounts/restore/{id}", account.getId())
                        .sessionAttr("loginUser", "admin@test.com")
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts/deleted"));

        Account restoredAccount =
                accountRepository.findById(account.getId())
                        .orElseThrow();

        assertThat(restoredAccount.getDeleted())
                .isEqualTo("false");

        assertThat(restoredAccount.getDeletedAt())
                .isNull();
    }

    @Test
    void changeStatusTogglesAccountStatus()
            throws Exception {

        Account account =
                accountRepository.save(new Account(
                        "USER",
                        "一般ユーザー",
                        "user@test.com",
                        "password1",
                        "アクセス許可",
                        "false"));

        mockMvc.perform(post("/accounts/status/{id}", account.getId())
                        .sessionAttr("loginUser", "admin@test.com")
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts"));

        Account updatedAccount =
                accountRepository.findById(account.getId())
                        .orElseThrow();

        assertThat(updatedAccount.getStatus())
                .isEqualTo("アクセス禁止");
    }

    @Test
    void accountOperationRedirectsWhenNotAdmin()
            throws Exception {

        mockMvc.perform(post("/accounts/delete/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
