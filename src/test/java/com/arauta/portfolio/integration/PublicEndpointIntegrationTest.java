package com.arauta.portfolio.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 公開頁面的端對端整合測試。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PublicEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ──────────────────────────────────────────
    // 所有公開路由無需認證可訪問（參數化）
    // ──────────────────────────────────────────

    @ParameterizedTest
    @ValueSource(strings = {"/public/", "/public/projects", "/public/skills", "/public/experience", "/public/lab"})
    void allPublicEndpoints_unauthenticated_return200(String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isOk());
    }

    // ──────────────────────────────────────────
    // XSS 防護驗證（資料庫 round-trip）
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProjectWithXssPayload_publicPageEscapesScript() throws Exception {
        // 1. Admin 建立含 XSS 內容的專案
        String xssContent = "<script>alert('xss')</script>";
        mockMvc.perform(post("/admin/project/save").with(csrf())
                        .param("title", "XSS Test Project")
                        .param("content", xssContent))
                .andExpect(status().is3xxRedirection());

        // 2. 公開頁面渲染時，script tag 不應出現為原始 HTML
        mockMvc.perform(get("/public/projects"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("<script>alert('xss')</script>"))));
    }

    // ──────────────────────────────────────────
    // 頁面基本結構驗證
    // ──────────────────────────────────────────

    @Test
    void homePage_returns200AndHasHtmlStructure() throws Exception {
        mockMvc.perform(get("/public/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    void projectsPage_emptyDb_renders200WithoutError() throws Exception {
        // 空資料庫時頁面不崩潰
        mockMvc.perform(get("/public/projects"))
                .andExpect(status().isOk());
    }

    @Test
    void experiencePage_emptyDb_renders200WithoutError() throws Exception {
        mockMvc.perform(get("/public/experience"))
                .andExpect(status().isOk());
    }

    @Test
    void skillsPage_emptyDb_renders200WithoutError() throws Exception {
        mockMvc.perform(get("/public/skills"))
                .andExpect(status().isOk());
    }

    @Test
    void labPage_emptyDb_renders200WithoutError() throws Exception {
        mockMvc.perform(get("/public/lab"))
                .andExpect(status().isOk());
    }
}
