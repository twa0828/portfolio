package com.example.demo;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;

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
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {

        categoryRepository.deleteAll();
    }

    @Test
    void createCategorySavesCategory()
            throws Exception {

        mockMvc.perform(post("/categories/create")
                        .sessionAttr("loginUser", "admin@test.com")
                        .sessionAttr("role", "ADMIN")
                        .param("name", "質問"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));

        assertThat(categoryRepository.findAll())
                .hasSize(1);

        Category category =
                categoryRepository.findAll().get(0);

        assertThat(category.getName())
                .isEqualTo("質問");

        assertThat(category.getDeleted())
                .isEqualTo("false");
    }

    @Test
    void createCategoryShowsErrorWhenNameIsTooLong()
            throws Exception {

        String longName =
                "あ".repeat(256);

        mockMvc.perform(post("/categories/create")
                        .sessionAttr("loginUser", "admin@test.com")
                        .sessionAttr("role", "ADMIN")
                        .param("name", longName))
                .andExpect(status().isOk())
                .andExpect(view().name("category-create"))
                .andExpect(model().attribute(
                        "errorMessage",
                        "名前は255文字以内です"));

        assertThat(categoryRepository.findAll())
                .isEmpty();
    }

    @Test
    void updateCategoryChangesName()
            throws Exception {

        Category category =
                categoryRepository.save(new Category(
                        "変更前"));

        mockMvc.perform(post("/categories/update/{id}", category.getId())
                        .sessionAttr("loginUser", "admin@test.com")
                        .sessionAttr("role", "ADMIN")
                        .param("name", "変更後"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));

        Category updatedCategory =
                categoryRepository.findById(category.getId())
                        .orElseThrow();

        assertThat(updatedCategory.getName())
                .isEqualTo("変更後");
    }

    @Test
    void deleteCategoryMarksCategoryAsDeleted()
            throws Exception {

        Category category =
                categoryRepository.save(new Category(
                        "質問"));

        mockMvc.perform(post("/categories/delete/{id}", category.getId())
                        .sessionAttr("loginUser", "admin@test.com")
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));

        Category deletedCategory =
                categoryRepository.findById(category.getId())
                        .orElseThrow();

        assertThat(deletedCategory.getDeleted())
                .isEqualTo("true");

        assertThat(deletedCategory.getDeletedAt())
                .isNotNull();
    }

    @Test
    void categoryOperationRedirectsWhenNotAdmin()
            throws Exception {

        mockMvc.perform(post("/categories/create")
                        .param("name", "質問"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
