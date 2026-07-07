package com.example.demo;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {

        accountRepository.deleteAll();
    }

    @Test
    void adminDashboardShowsUserLikeRankingsFromDatabase()
            throws Exception {

        Account firstUser = new Account(
                "USER",
                "年間一位ユーザー",
                "first@test.com",
                "password1",
                "アクセス許可",
                "false");
        firstUser.setAnnualLikes(30);
        firstUser.setMonthlyLikes(5);

        Account secondUser = new Account(
                "USER",
                "月間一位ユーザー",
                "second@test.com",
                "password1",
                "アクセス許可",
                "false");
        secondUser.setAnnualLikes(10);
        secondUser.setMonthlyLikes(40);

        Account admin = new Account(
                "ADMIN",
                "ランキング対象外管理者",
                "admin@test.com",
                "password1",
                "アクセス許可",
                "false");

        Account stoppedUser = new Account(
                "USER",
                "アクセス禁止ユーザー",
                "stopped@test.com",
                "password1",
                "アクセス禁止",
                "false");

        accountRepository.save(firstUser);
        accountRepository.save(secondUser);
        accountRepository.save(admin);
        accountRepository.save(stoppedUser);

        mockMvc.perform(get("/admin")
                        .sessionAttr("loginUser", "admin@test.com")
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("年間一位ユーザー")))
                .andExpect(content().string(containsString("月間一位ユーザー")))
                .andExpect(content().string(containsString("30")))
                .andExpect(content().string(containsString("40")))
                .andExpect(content().string(not(containsString("ランキング対象外管理者"))))
                .andExpect(content().string(not(containsString("アクセス禁止ユーザー"))));
    }

    @Test
    void adminDashboardRedirectsWhenNotLoggedIn()
            throws Exception {

        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection());
    }
}
