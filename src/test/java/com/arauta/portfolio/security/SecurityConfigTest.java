package com.arauta.portfolio.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 驗證 SecurityConfig 的存取控制規則。
 * 使用 @SpringBootTest 確保載入的是真實的 SecurityFilterChain，
 * 而非 @WebMvcTest 預設的安全配置。
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // ──────────────────────────────────────────
    // 公開路由：無需登入
    // ──────────────────────────────────────────

    @Test
    void publicHome_unauthenticated_returns200() throws Exception {
        mockMvc.perform(get("/public/"))
                .andExpect(status().isOk());
    }

    @Test
    void publicProjects_unauthenticated_returns200() throws Exception {
        mockMvc.perform(get("/public/projects"))
                .andExpect(status().isOk());
    }

    @Test
    void publicSkills_unauthenticated_returns200() throws Exception {
        mockMvc.perform(get("/public/skills"))
                .andExpect(status().isOk());
    }

    @Test
    void publicExperience_unauthenticated_returns200() throws Exception {
        mockMvc.perform(get("/public/experience"))
                .andExpect(status().isOk());
    }

    @Test
    void publicLab_unauthenticated_returns200() throws Exception {
        mockMvc.perform(get("/public/lab"))
                .andExpect(status().isOk());
    }

    @Test
    void loginPage_unauthenticated_returns200() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void registerPage_unauthenticated_returns200() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    // ──────────────────────────────────────────
    // 需要登入的路由
    // ──────────────────────────────────────────

    @Test
    void changePassword_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/change-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser
    void changePassword_authenticated_returns200() throws Exception {
        mockMvc.perform(get("/change-password"))
                .andExpect(status().isOk());
    }

    // ──────────────────────────────────────────
    // Admin 路由：需要 ROLE_ADMIN
    // ──────────────────────────────────────────

    @Test
    void adminProject_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/project"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminProject_asRoleUser_returns403() throws Exception {
        mockMvc.perform(get("/admin/project"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminProject_asRoleAdmin_returns200() throws Exception {
        mockMvc.perform(get("/admin/project"))
                .andExpect(status().isOk());
    }

    @Test
    void adminHomepage_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/homepage"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminHomepage_asRoleUser_returns403() throws Exception {
        mockMvc.perform(get("/admin/homepage"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminHomepage_asRoleAdmin_returns200() throws Exception {
        mockMvc.perform(get("/admin/homepage"))
                .andExpect(status().isOk());
    }

    @Test
    void adminExperience_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/experience"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminExperience_asRoleUser_returns403() throws Exception {
        mockMvc.perform(get("/admin/experience"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminExperience_asRoleAdmin_returns200() throws Exception {
        mockMvc.perform(get("/admin/experience"))
                .andExpect(status().isOk());
    }

    @Test
    void adminSkills_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/skills"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminSkills_asRoleUser_returns403() throws Exception {
        mockMvc.perform(get("/admin/skills"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminSkills_asRoleAdmin_returns200() throws Exception {
        mockMvc.perform(get("/admin/skills"))
                .andExpect(status().isOk());
    }

    @Test
    void adminLab_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/lab"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminLab_asRoleUser_returns403() throws Exception {
        mockMvc.perform(get("/admin/lab"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminLab_asRoleAdmin_returns200() throws Exception {
        mockMvc.perform(get("/admin/lab"))
                .andExpect(status().isOk());
    }

    // ──────────────────────────────────────────
    // CSRF 保護：POST 需要 CSRF Token
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postWithoutCsrf_returns403() throws Exception {
        mockMvc.perform(post("/admin/project/save")
                .param("title", "Test"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postWithCsrf_asAdmin_doesNotReturn403() throws Exception {
        mockMvc.perform(post("/admin/project/save")
                .with(csrf())
                .param("title", "Test"))
                .andExpect(status().is3xxRedirection()); // redirect:/admin/project?saved
    }
}
