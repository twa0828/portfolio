package com.example.demo;

import com.example.demo.model.Contact;
import com.example.demo.repository.ContactRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactRepository contactRepository;

    @BeforeEach
    void setUp() {

        contactRepository.deleteAll();
    }

    @Test
    void contactListShowsActiveContacts()
            throws Exception {

        contactRepository.save(new Contact(
                "質問",
                "表示されるお問い合わせです",
                "未対応"));

        Contact deletedContact =
                new Contact(
                        "質問",
                        "削除済みお問い合わせです",
                        "未対応");
        deletedContact.setDeleted("true");
        contactRepository.save(deletedContact);

        mockMvc.perform(get("/contacts")
                        .sessionAttr("loginUser", "admin@test.com")
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("contacts"))
                .andExpect(content().string(containsString("表示されるお問い合わ")))
                .andExpect(content().string(not(containsString("削除済みお問い合わせ"))));
    }

    @Test
    void contactDetailShowsContactContent()
            throws Exception {

        Contact contact =
                contactRepository.save(new Contact(
                        "質問",
                        "1行目\n2行目",
                        "未対応"));

        mockMvc.perform(get("/contacts/{id}", contact.getId())
                        .sessionAttr("loginUser", "admin@test.com")
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact-detail"))
                .andExpect(content().string(containsString("質問")))
                .andExpect(content().string(containsString("1行目")))
                .andExpect(content().string(containsString("2行目")))
                .andExpect(content().string(containsString("未対応")));
    }

    @Test
    void updateContactStatusChangesStatus()
            throws Exception {

        Contact contact =
                contactRepository.save(new Contact(
                        "質問",
                        "お問い合わせ内容です",
                        "未対応"));

        mockMvc.perform(post("/contacts/status/{id}", contact.getId())
                        .sessionAttr("loginUser", "admin@test.com")
                        .sessionAttr("role", "ADMIN")
                        .param("status", "対応済み"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contacts/" + contact.getId()));

        Contact updatedContact =
                contactRepository.findById(contact.getId())
                        .orElseThrow();

        assertThat(updatedContact.getStatus())
                .isEqualTo("対応済み");
    }

    @Test
    void invalidStatusDoesNotChangeStatus()
            throws Exception {

        Contact contact =
                contactRepository.save(new Contact(
                        "質問",
                        "お問い合わせ内容です",
                        "未対応"));

        mockMvc.perform(post("/contacts/status/{id}", contact.getId())
                        .sessionAttr("loginUser", "admin@test.com")
                        .sessionAttr("role", "ADMIN")
                        .param("status", "不正なステータス"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contacts/" + contact.getId()));

        Contact updatedContact =
                contactRepository.findById(contact.getId())
                        .orElseThrow();

        assertThat(updatedContact.getStatus())
                .isEqualTo("未対応");
    }

    @Test
    void contactOperationRedirectsWhenNotAdmin()
            throws Exception {

        mockMvc.perform(get("/contacts"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
