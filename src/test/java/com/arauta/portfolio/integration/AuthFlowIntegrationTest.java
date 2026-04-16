package com.arauta.portfolio.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 完整認證流程的整合測試。
 * 每個測試方法使用 @Transactional 自動回滾，保持 DB 隔離。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ──────────────────────────────────────────
    // 註冊 → 登入 完整流程
    // ──────────────────────────────────────────

    @Test
    void register_thenLoginWithSameCredentials_succeeds() throws Exception {
        // 1. 先註冊
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "integrationuser")
                        .param("password", "testpass1")
                        .param("confirmPassword", "testpass1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // 2. 以相同帳密登入（Spring Security form login endpoint）
        mockMvc.perform(post("/login").with(csrf())
                        .param("username", "integrationuser")
                        .param("password", "testpass1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/public/"));
    }

    @Test
    void register_thenLoginWithWrongPassword_fails() throws Exception {
        // 1. 先註冊
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "user2")
                        .param("password", "correctpass")
                        .param("confirmPassword", "correctpass"))
                .andExpect(status().is3xxRedirection());

        // 2. 以錯誤密碼登入
        mockMvc.perform(post("/login").with(csrf())
                        .param("username", "user2")
                        .param("password", "wrongpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void register_duplicateUsername_showsError() throws Exception {
        // 1. 第一次註冊
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "dupuser")
                        .param("password", "password1")
                        .param("confirmPassword", "password1"))
                .andExpect(redirectedUrl("/login"));

        // 2. 第二次使用相同帳號
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "dupuser")
                        .param("password", "password2")
                        .param("confirmPassword", "password2"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("usernameExists", true));
    }

    @Test
    void register_withBlankUsername_failsValidation() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "")
                        .param("password", "password1")
                        .param("confirmPassword", "password1"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }
}
