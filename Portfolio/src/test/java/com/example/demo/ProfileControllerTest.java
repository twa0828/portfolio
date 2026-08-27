package com.example.demo;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {

        accountRepository.deleteAll();
    }

    @Test
    void updateProfileSavesUserProfile()
            throws Exception {

        accountRepository.save(new Account(
                "USER",
                "更新前",
                "user@test.com",
                "password1",
                "アクセス許可",
                "false"));

        MockMultipartFile profileImage =
                new MockMultipartFile(
                        "profileImage",
                        "profile.png",
                        "image/png",
                        "image-data".getBytes());

        mockMvc.perform(multipart("/profile/update")
                        .file(profileImage)
                        .sessionAttr("loginUser", "user@test.com")
                        .sessionAttr("role", "USER")
                        .param("name", "更新後ユーザー")
                        .param("furigana", "こうしんごゆーざー")
                        .param("gender", "男性")
                        .param("age", "25")
                        .param("profile", "自己紹介です"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute(
                        "successMessage",
                        "プロフィールを更新しました"));

        Account account =
                accountRepository.findByEmail("user@test.com")
                        .orElseThrow();

        assertThat(account.getName())
                .isEqualTo("更新後ユーザー");

        assertThat(account.getFurigana())
                .isEqualTo("こうしんごゆーざー");

        assertThat(account.getGender())
                .isEqualTo("男性");

        assertThat(account.getAge())
                .isEqualTo("25");

        assertThat(account.getProfile())
                .isEqualTo("自己紹介です");

        assertThat(account.getProfileImage())
                .isEqualTo("image-data".getBytes());

        assertThat(account.getProfileImageContentType())
                .isEqualTo("image/png");
    }

    @Test
    void updateProfileDoesNotSaveWhenFuriganaIsInvalid()
            throws Exception {

        accountRepository.save(new Account(
                "USER",
                "更新前",
                "user@test.com",
                "password1",
                "アクセス許可",
                "false"));

        mockMvc.perform(multipart("/profile/update")
                        .sessionAttr("loginUser", "user@test.com")
                        .sessionAttr("role", "USER")
                        .param("name", "更新後ユーザー")
                        .param("furigana", "カタカナ")
                        .param("gender", "男性")
                        .param("age", "25")
                        .param("profile", "自己紹介です"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute(
                        "errorMessage",
                        "ふりがなはひらがなのみです"));

        Account account =
                accountRepository.findByEmail("user@test.com")
                        .orElseThrow();

        assertThat(account.getName())
                .isEqualTo("更新前");

        assertThat(account.getFurigana())
                .isNull();
    }

    @Test
    void updateProfileRejectsImageLargerThanTwoMegabytes()
            throws Exception {

        accountRepository.save(new Account(
                "USER",
                "更新前",
                "user@test.com",
                "password1",
                "アクセス許可",
                "false"));

        MockMultipartFile profileImage =
                new MockMultipartFile(
                        "profileImage",
                        "large.png",
                        "image/png",
                        new byte[2 * 1024 * 1024 + 1]);

        mockMvc.perform(multipart("/profile/update")
                        .file(profileImage)
                        .sessionAttr("loginUser", "user@test.com")
                        .sessionAttr("role", "USER")
                        .param("name", "更新後ユーザー")
                        .param("furigana", "こうしんごゆーざー")
                        .param("gender", "男性")
                        .param("age", "25")
                        .param("profile", "自己紹介です"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute(
                        "errorMessage",
                        "プロフィール画像は2MB以内です"));

        Account account =
                accountRepository.findByEmail("user@test.com")
                        .orElseThrow();

        assertThat(account.getName())
                .isEqualTo("更新前");

        assertThat(account.getProfileImage())
                .isNull();
    }

    @Test
    void updateProfileRedirectsWhenNotLoggedIn()
            throws Exception {

        mockMvc.perform(multipart("/profile/update")
                        .param("name", "更新後ユーザー")
                        .param("furigana", "こうしんごゆーざー")
                        .param("gender", "男性")
                        .param("age", "25")
                        .param("profile", "自己紹介です"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void profileImageReturnsSavedImage()
            throws Exception {

        Account account =
                new Account(
                        "USER",
                        "ユーザー",
                        "user@test.com",
                        "password1",
                        "アクセス許可",
                        "false");

        account.setProfileImage(
                "image-data".getBytes());

        account.setProfileImageContentType(
                "image/png");

        accountRepository.save(account);

        mockMvc.perform(get("/profile/image")
                        .sessionAttr("loginUser", "user@test.com")
                        .sessionAttr("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes("image-data".getBytes()));
    }
}
