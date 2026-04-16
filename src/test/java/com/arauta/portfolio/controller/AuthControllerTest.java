package com.arauta.portfolio.controller;

import com.arauta.portfolio.config.SecurityConfig;
import com.arauta.portfolio.service.ContentService;
import com.arauta.portfolio.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @Import(SecurityConfig.class)：讓 /login 與 /register 路由的許可規則生效，
//   避免 DefaultLoginPageGeneratingFilter 攔截 /login 而繞過 AuthController。
// class-level @WithMockUser：讓其餘需要認證的路由（/change-password）通過。
// 路由安全規則的完整測試由 SecurityConfigTest（@SpringBootTest）負責驗證。
@WithMockUser
@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // DrawerControllerAdvice 作用於 HomeController，但 @WebMvcTest 仍會載入所有
    // @ControllerAdvice bean，因此需要提供其依賴 ContentService
    @MockBean
    private ContentService contentService;

    // ──────────────────────────────────────────
    // GET /login
    // ──────────────────────────────────────────

    @Test
    void getLoginPage_returns200AndLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    // ──────────────────────────────────────────
    // GET /register
    // ──────────────────────────────────────────

    @Test
    void getRegisterPage_returns200AndRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("form"));
    }

    // ──────────────────────────────────────────
    // POST /register
    // ──────────────────────────────────────────

    @Test
    void postRegister_validNewUser_redirectsToLogin() throws Exception {
        doNothing().when(userService).register(anyString(), anyString());

        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void postRegister_existingUsername_returnsRegisterViewWithFlag() throws Exception {
        doThrow(new UserService.UsernameAlreadyExistsException())
                .when(userService).register(anyString(), anyString());

        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "existinguser")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("usernameExists", true));
    }

    @Test
    void postRegister_passwordMismatch_returnsRegisterViewWithFlag() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("confirmPassword", "different123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("passwordMismatch", true));
    }

    @Test
    void postRegister_blankUsername_failsValidation() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());
    }

    @Test
    void postRegister_shortPassword_failsValidation() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "validuser")
                        .param("password", "short")    // < 8 chars
                        .param("confirmPassword", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());
    }

    @Test
    void postRegister_tooShortUsername_failsValidation() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "ab")        // < 3 chars
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());
    }

    @Test
    void postRegister_usernameTooLong_failsValidation() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "a".repeat(21))  // > 20 chars
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("form", "username"));
    }

    @Test
    void postRegister_passwordTooLong_failsValidation() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "validuser")
                        .param("password", "a".repeat(21))  // > 20 chars
                        .param("confirmPassword", "a".repeat(21)))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("form", "password"));
    }

    // ──────────────────────────────────────────
    // GET /change-password
    // ──────────────────────────────────────────

    @Test
    @WithMockUser
    void getChangePassword_authenticated_returns200() throws Exception {
        mockMvc.perform(get("/change-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/change-password"));
    }

    // ──────────────────────────────────────────
    // POST /change-password
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(username = "testuser")
    void postChangePassword_correctPassword_redirectsWithSuccess() throws Exception {
        doNothing().when(userService).changePassword(anyString(), anyString(), anyString());

        mockMvc.perform(post("/change-password").with(csrf())
                        .param("oldPassword", "oldPass123")
                        .param("newPassword", "newPass123")
                        .param("confirmPassword", "newPass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/change-password?success"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void postChangePassword_wrongCurrentPassword_returnsViewWithFlag() throws Exception {
        doThrow(new UserService.WrongPasswordException())
                .when(userService).changePassword(anyString(), anyString(), anyString());

        mockMvc.perform(post("/change-password").with(csrf())
                        .param("oldPassword", "wrongPass")
                        .param("newPassword", "newPass123")
                        .param("confirmPassword", "newPass123"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("wrongPassword", true));
    }

    @Test
    @WithMockUser(username = "testuser")
    void postChangePassword_newPasswordMismatch_returnsViewWithFlag() throws Exception {
        mockMvc.perform(post("/change-password").with(csrf())
                        .param("oldPassword", "oldPass123")
                        .param("newPassword", "newPass123")
                        .param("confirmPassword", "differentPass"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("confirmMismatch", true));
    }

    @Test
    @WithMockUser(username = "testuser")
    void postChangePassword_newPasswordTooShort_failsValidation() throws Exception {
        mockMvc.perform(post("/change-password").with(csrf())
                        .param("oldPassword", "oldPass123")
                        .param("newPassword", "short7")     // < 8 chars
                        .param("confirmPassword", "short7"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "newPassword"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void postChangePassword_newPasswordTooLong_failsValidation() throws Exception {
        mockMvc.perform(post("/change-password").with(csrf())
                        .param("oldPassword", "oldPass123")
                        .param("newPassword", "a".repeat(21))   // > 20 chars
                        .param("confirmPassword", "a".repeat(21)))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "newPassword"));
    }
}
