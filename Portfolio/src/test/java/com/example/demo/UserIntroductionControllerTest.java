package com.example.demo;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class UserIntroductionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    private Account user;

    @BeforeEach
    void setUp() {

        accountRepository.deleteAll();

        user = accountRepository.save(new Account(
                "USER",
                "公開ユーザー",
                "public@test.com",
                "password1",
                "アクセス許可",
                "false"));
    }

    @Test
    void introductionsCanBeViewedWithoutLogin()
            throws Exception {

        mockMvc.perform(get("/introductions"))
                .andExpect(status().isOk())
                .andExpect(view().name("introductions"))
                .andExpect(model().attribute("loggedInUser", false))
                .andExpect(model().attribute("userList", hasSize(1)));
    }

    @Test
    void likeRankingCanBeViewedWithoutLogin()
            throws Exception {

        mockMvc.perform(get("/like-ranking"))
                .andExpect(status().isOk())
                .andExpect(view().name("like-ranking"))
                .andExpect(model().attribute("loggedInUser", false))
                .andExpect(model().attribute("userList", hasSize(1)));
    }

    @Test
    void introductionDetailCanBeViewedWithoutLogin()
            throws Exception {

        mockMvc.perform(get("/introductions/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("introduction-detail"))
                .andExpect(model().attribute("loggedInUser", false))
                .andExpect(model().attribute(
                        "account",
                        hasProperty("id", is(user.getId()))));
    }

    @Test
    void likeCanBeAddedWithoutLogin()
            throws Exception {

        mockMvc.perform(post("/introductions/like/" + user.getId()))
                .andExpect(status().isOk());

        Account updatedAccount =
                accountRepository.findById(user.getId())
                        .orElseThrow();

        assertThat(updatedAccount.getAnnualLikes()).isEqualTo(1);
        assertThat(updatedAccount.getMonthlyLikes()).isEqualTo(1);
    }
}
