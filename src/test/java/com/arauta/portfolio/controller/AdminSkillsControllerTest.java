package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.Section;
import com.arauta.portfolio.service.ContentService;
import com.arauta.portfolio.service.SectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminSkillsController.class)
class AdminSkillsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SectionService sectionService;

    // DrawerControllerAdvice 的依賴，@WebMvcTest 必須提供
    @MockBean
    private ContentService contentService;

    // ──────────────────────────────────────────
    // GET /admin/skills
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminSkills_asAdmin_returns200() throws Exception {
        when(sectionService.getGroupedSections(anyString())).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/admin/skills"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/skills"))
                .andExpect(model().attributeExists("groupedSections", "groupTypes"));
    }

    // ──────────────────────────────────────────
    // POST /admin/skills/section/create
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postCreateSection_asAdmin_redirectsToEditPage() throws Exception {
        Section created = new Section("skills", "lang", Section.GroupType.SKILL_ROW, 0);
        when(sectionService.addCard(anyString(), anyString(), any(Section.GroupType.class)))
                .thenReturn(created);

        mockMvc.perform(post("/admin/skills/section/create").with(csrf())
                        .param("groupKey", "lang")
                        .param("groupType", "SKILL_ROW"))
                .andExpect(status().is3xxRedirection());
    }

    // ──────────────────────────────────────────
    // GET /admin/skills/section/{id}/edit
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEditSection_asAdmin_returns200() throws Exception {
        Section section = new Section("skills", "lang", Section.GroupType.SKILL_ROW, 0);
        when(sectionService.getById(1L)).thenReturn(section);

        mockMvc.perform(get("/admin/skills/section/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/section-edit"));
    }

    // ──────────────────────────────────────────
    // POST /admin/skills/section/{id}/save
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveSection_asAdmin_redirectsToSkills() throws Exception {
        Section section = new Section("skills", "lang", Section.GroupType.SKILL_ROW, 0);
        when(sectionService.getById(1L)).thenReturn(section);

        mockMvc.perform(post("/admin/skills/section/1/save").with(csrf())
                        .param("title", "Languages"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/skills?saved"));
    }

    // ──────────────────────────────────────────
    // Validation — POST /admin/skills/section/{id}/save
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveSection_titleTooLong_returnsEditFormWithErrors() throws Exception {
        Section section = new Section("skills", "lang", Section.GroupType.SKILL_ROW, 0);
        when(sectionService.getById(1L)).thenReturn(section);

        mockMvc.perform(post("/admin/skills/section/1/save").with(csrf())
                        .param("title", "a".repeat(201)))  // > 200 chars
                .andExpect(status().isOk())
                .andExpect(view().name("admin/section-edit"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "title"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveSection_groupKeyTooLong_returnsEditFormWithErrors() throws Exception {
        Section section = new Section("skills", "lang", Section.GroupType.SKILL_ROW, 0);
        when(sectionService.getById(1L)).thenReturn(section);

        mockMvc.perform(post("/admin/skills/section/1/save").with(csrf())
                        .param("groupKey", "a".repeat(51)))  // > 50 chars
                .andExpect(status().isOk())
                .andExpect(view().name("admin/section-edit"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "groupKey"));
    }

    // ──────────────────────────────────────────
    // POST /admin/skills/section/{id}/delete
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postDeleteSection_asAdmin_redirectsToSkills() throws Exception {
        mockMvc.perform(post("/admin/skills/section/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/skills?deleted"));

        verify(sectionService).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postDeleteSection_withoutCsrf_returns403() throws Exception {
        mockMvc.perform(post("/admin/skills/section/1/delete"))
                .andExpect(status().isForbidden());
    }
}
