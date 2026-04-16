package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.Experience;
import com.arauta.portfolio.service.ContentService;
import com.arauta.portfolio.service.ExperienceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminExperienceController.class)
class AdminExperienceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExperienceService experienceService;

    // DrawerControllerAdvice 的依賴，@WebMvcTest 必須提供
    @MockBean
    private ContentService contentService;

    // ──────────────────────────────────────────
    // GET /admin/experience
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminExperience_asAdmin_returns200() throws Exception {
        when(experienceService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/experience"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/experience"))
                .andExpect(model().attributeExists("experienceList"));
    }

    // ──────────────────────────────────────────
    // GET /admin/experience/{id}/edit
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEditExperience_asAdmin_returns200() throws Exception {
        Experience exp = new Experience("2023", "Title", "body", 0);
        when(experienceService.getById(1L)).thenReturn(exp);

        mockMvc.perform(get("/admin/experience/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/experience-edit"))
                .andExpect(model().attributeExists("item"));
    }

    // ──────────────────────────────────────────
    // POST /admin/experience/create
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postCreateExperience_asAdmin_redirectsToList() throws Exception {
        when(experienceService.nextSortOrder()).thenReturn(0);
        when(experienceService.save(any(Experience.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/admin/experience/create").with(csrf())
                        .param("year", "2024")
                        .param("title", "New Job")
                        .param("body", "Description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/experience?saved"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postCreateExperience_withoutCsrf_returns403() throws Exception {
        mockMvc.perform(post("/admin/experience/create")
                        .param("year", "2024")
                        .param("title", "Title"))
                .andExpect(status().isForbidden());
    }

    // ──────────────────────────────────────────
    // POST /admin/experience/{id}/save (update)
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveExperience_asAdmin_redirectsToList() throws Exception {
        Experience exp = new Experience("2022", "Old", "body", 0);
        when(experienceService.getById(1L)).thenReturn(exp);
        when(experienceService.save(any())).thenReturn(exp);

        mockMvc.perform(post("/admin/experience/1/save").with(csrf())
                        .param("year", "2023")
                        .param("title", "Updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/experience?saved"));
    }

    // ──────────────────────────────────────────
    // POST /admin/experience/{id}/delete
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postDeleteExperience_asAdmin_redirectsToList() throws Exception {
        mockMvc.perform(post("/admin/experience/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/experience?deleted"));

        verify(experienceService).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postDeleteExperience_withoutCsrf_returns403() throws Exception {
        mockMvc.perform(post("/admin/experience/1/delete"))
                .andExpect(status().isForbidden());
    }

    // ──────────────────────────────────────────
    // Validation — POST /admin/experience/create
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postCreateExperience_blankYear_returnsFormWithErrors() throws Exception {
        when(experienceService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/experience/create").with(csrf())
                        .param("year", "")
                        .param("title", "Some Title"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/experience"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "year"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postCreateExperience_blankTitle_returnsFormWithErrors() throws Exception {
        when(experienceService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/experience/create").with(csrf())
                        .param("year", "2024")
                        .param("title", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/experience"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "title"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postCreateExperience_yearTooLong_returnsFormWithErrors() throws Exception {
        when(experienceService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/experience/create").with(csrf())
                        .param("year", "a".repeat(5))  // > 4 chars
                        .param("title", "Valid Title"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/experience"))
                .andExpect(model().attributeHasFieldErrors("form", "year"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postCreateExperience_titleTooLong_returnsFormWithErrors() throws Exception {
        when(experienceService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/experience/create").with(csrf())
                        .param("year", "2024")
                        .param("title", "a".repeat(201)))  // > 200 chars
                .andExpect(status().isOk())
                .andExpect(view().name("admin/experience"))
                .andExpect(model().attributeHasFieldErrors("form", "title"));
    }

    // ──────────────────────────────────────────
    // Validation — POST /admin/experience/{id}/save (update)
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveExperience_blankTitle_returnsEditFormWithErrors() throws Exception {
        Experience exp = new Experience("2022", "Old", "body", 0);
        when(experienceService.getById(1L)).thenReturn(exp);

        mockMvc.perform(post("/admin/experience/1/save").with(csrf())
                        .param("year", "2023")
                        .param("title", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/experience-edit"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "title"));
    }

    // ──────────────────────────────────────────
    // 確認 nextSortOrder() 被用於建立時
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postCreateExperience_usesNextSortOrderFromService() throws Exception {
        when(experienceService.nextSortOrder()).thenReturn(3);
        when(experienceService.save(any())).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/admin/experience/create").with(csrf())
                        .param("year", "2024")
                        .param("title", "Title"))
                .andExpect(status().is3xxRedirection());

        verify(experienceService).nextSortOrder();
    }
}
