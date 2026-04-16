package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.LabEntry;
import com.arauta.portfolio.service.ContentService;
import com.arauta.portfolio.service.LabService;
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

@WebMvcTest(AdminLabController.class)
class AdminLabControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LabService labService;

    // DrawerControllerAdvice 的依賴，@WebMvcTest 必須提供
    @MockBean
    private ContentService contentService;

    // ──────────────────────────────────────────
    // GET /admin/lab
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminLab_asAdmin_returns200() throws Exception {
        when(labService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/lab"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/lab"))
                .andExpect(model().attributeExists("labEntries"));
    }

    // ──────────────────────────────────────────
    // GET /admin/lab/{id}/edit
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEditLab_asAdmin_returns200() throws Exception {
        LabEntry entry = new LabEntry("Tool");
        when(labService.getById(1L)).thenReturn(entry);

        mockMvc.perform(get("/admin/lab/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/lab-edit"))
                .andExpect(model().attributeExists("item"));
    }

    // ──────────────────────────────────────────
    // POST /admin/lab/save (create)
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveLab_asAdmin_redirectsToList() throws Exception {
        LabEntry entry = new LabEntry("Tool");
        when(labService.prepareNew(anyString(), any(), any(), any())).thenReturn(entry);
        doNothing().when(labService).saveWithTags(any(), any());

        mockMvc.perform(post("/admin/lab/save").with(csrf())
                        .param("name", "New Tool"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/lab?saved"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveLab_withoutCsrf_returns403() throws Exception {
        mockMvc.perform(post("/admin/lab/save")
                        .param("name", "Tool"))
                .andExpect(status().isForbidden());
    }

    // ──────────────────────────────────────────
    // POST /admin/lab/{id}/save (update)
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postUpdateLab_asAdmin_redirectsToList() throws Exception {
        LabEntry entry = new LabEntry("Old Tool");
        when(labService.getById(1L)).thenReturn(entry);
        doNothing().when(labService).saveWithTags(any(), any());

        mockMvc.perform(post("/admin/lab/1/save").with(csrf())
                        .param("name", "Updated Tool"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/lab?saved"));
    }

    // ──────────────────────────────────────────
    // Validation — POST /admin/lab/save (create)
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveLab_blankName_returnsFormWithErrors() throws Exception {
        when(labService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/lab/save").with(csrf())
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/lab"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveLab_nameTooLong_returnsFormWithErrors() throws Exception {
        when(labService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/lab/save").with(csrf())
                        .param("name", "a".repeat(101)))  // > 100 chars
                .andExpect(status().isOk())
                .andExpect(view().name("admin/lab"))
                .andExpect(model().attributeHasFieldErrors("form", "name"));
    }

    // ──────────────────────────────────────────
    // Validation — POST /admin/lab/{id}/save (update)
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postUpdateLab_blankName_returnsEditFormWithErrors() throws Exception {
        LabEntry entry = new LabEntry("Old Tool");
        when(labService.getById(1L)).thenReturn(entry);

        mockMvc.perform(post("/admin/lab/1/save").with(csrf())
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/lab-edit"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "name"));
    }

    // ──────────────────────────────────────────
    // POST /admin/lab/{id}/delete
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postDeleteLab_asAdmin_redirectsToList() throws Exception {
        mockMvc.perform(post("/admin/lab/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/lab?deleted"));

        verify(labService).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postDeleteLab_withoutCsrf_returns403() throws Exception {
        mockMvc.perform(post("/admin/lab/1/delete"))
                .andExpect(status().isForbidden());
    }
}
