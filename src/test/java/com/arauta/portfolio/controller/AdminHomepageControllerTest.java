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

@WebMvcTest(AdminHomepageController.class)
class AdminHomepageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SectionService sectionService;
    @MockBean
    private ContentService contentService;

    // ──────────────────────────────────────────
    // GET /admin/homepage
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminHomepage_asAdmin_returns200() throws Exception {
        when(sectionService.getGroupedSections(anyString())).thenReturn(new LinkedHashMap<>());
        when(contentService.getPageContent(anyString())).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/admin/homepage"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/homepage"))
                .andExpect(model().attributeExists("groupedSections", "content", "groupTypes"));
    }

    // ──────────────────────────────────────────
    // GET /admin/homepage/section/{id}/edit
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEditSection_asAdmin_existingSection_returns200() throws Exception {
        Section section = new Section("homepage", "about", Section.GroupType.SINGLE, 0);
        when(sectionService.getById(1L)).thenReturn(section);

        mockMvc.perform(get("/admin/homepage/section/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/section-edit"))
                .andExpect(model().attributeExists("section"));
    }

    // ──────────────────────────────────────────
    // POST /admin/homepage/section/create
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postCreateSection_asAdmin_redirectsToEditPage() throws Exception {
        Section created = new Section("homepage", "new-group", Section.GroupType.SINGLE, 0);
        // 設定假 ID（使用反射模擬或直接 return a section with an id）
        when(sectionService.addCard(anyString(), anyString(), any(Section.GroupType.class)))
                .thenReturn(created);

        mockMvc.perform(post("/admin/homepage/section/create").with(csrf())
                        .param("groupKey", "new-group")
                        .param("groupType", "SINGLE"))
                .andExpect(status().is3xxRedirection());
    }

    // ──────────────────────────────────────────
    // POST /admin/homepage/section/{id}/save
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveSection_asAdmin_validData_redirectsToHomepage() throws Exception {
        Section section = new Section("homepage", "about", Section.GroupType.SINGLE, 0);
        when(sectionService.getById(1L)).thenReturn(section);

        mockMvc.perform(post("/admin/homepage/section/1/save").with(csrf())
                        .param("title", "About Me")
                        .param("body", "Some content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/homepage?saved"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveSection_withoutCsrf_returns403() throws Exception {
        mockMvc.perform(post("/admin/homepage/section/1/save"))
                .andExpect(status().isForbidden());
    }

    // ──────────────────────────────────────────
    // Validation — POST /admin/homepage/section/{id}/save
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveSection_titleTooLong_returnsEditFormWithErrors() throws Exception {
        Section section = new Section("homepage", "about", Section.GroupType.SINGLE, 0);
        when(sectionService.getById(1L)).thenReturn(section);

        mockMvc.perform(post("/admin/homepage/section/1/save").with(csrf())
                        .param("title", "a".repeat(201)))  // > 200 chars
                .andExpect(status().isOk())
                .andExpect(view().name("admin/section-edit"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "title"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveSection_groupKeyTooLong_returnsEditFormWithErrors() throws Exception {
        Section section = new Section("homepage", "about", Section.GroupType.SINGLE, 0);
        when(sectionService.getById(1L)).thenReturn(section);

        mockMvc.perform(post("/admin/homepage/section/1/save").with(csrf())
                        .param("groupKey", "a".repeat(51)))  // > 50 chars
                .andExpect(status().isOk())
                .andExpect(view().name("admin/section-edit"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "groupKey"));
    }

    // ──────────────────────────────────────────
    // POST /admin/homepage/section/{id}/delete
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postDeleteSection_asAdmin_redirectsToHomepage() throws Exception {
        doNothing().when(sectionService).deleteById(1L);

        mockMvc.perform(post("/admin/homepage/section/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/homepage?deleted"));
    }

    // ──────────────────────────────────────────
    // POST /admin/homepage/rail/save
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveRail_asAdmin_redirectsWithSaved() throws Exception {
        mockMvc.perform(post("/admin/homepage/rail/save").with(csrf())
                        .param("rail.title", "My Title"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/homepage?saved"));

        verify(contentService).updatePageContent(anyString(), anyMap());
    }
}
