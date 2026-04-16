package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.Project;
import com.arauta.portfolio.service.ContentService;
import com.arauta.portfolio.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminProjectController.class)
class AdminProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    // DrawerControllerAdvice 的依賴，@WebMvcTest 必須提供
    @MockBean
    private ContentService contentService;

    // ──────────────────────────────────────────
    // GET /admin/project
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminProjects_asAdmin_returns200WithProjectList() throws Exception {
        when(projectService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/project"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/project-list"))
                .andExpect(model().attributeExists("projects"));
    }

    // ──────────────────────────────────────────
    // GET /admin/project/{id}/edit
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEditProject_asAdmin_existingProject_returns200() throws Exception {
        Project project = new Project("Test", "content", null);
        when(projectService.getById(1L)).thenReturn(project);

        mockMvc.perform(get("/admin/project/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/project-edit"))
                .andExpect(model().attributeExists("item"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEditProject_nonExistentProject_throwsIllegalArgument() {
        when(projectService.getById(99L)).thenThrow(new IllegalArgumentException("Project not found: 99"));

        // Spring 6 + MockMvc：Controller 未處理的例外會從 perform() 本身往外拋，
        // 用 assertThatThrownBy 驗證被包裝的根因。
        assertThatThrownBy(() -> mockMvc.perform(get("/admin/project/99/edit")))
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    // ──────────────────────────────────────────
    // POST /admin/project/save
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveProject_asAdmin_validData_redirectsToList() throws Exception {
        doNothing().when(projectService).createWithTags(any(), any(), any(), any(), any());

        mockMvc.perform(post("/admin/project/save").with(csrf())
                        .param("title", "New Project")
                        .param("content", "Some content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project?saved"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveProject_withoutCsrf_returns403() throws Exception {
        mockMvc.perform(post("/admin/project/save")
                        .param("title", "Project"))
                .andExpect(status().isForbidden());
    }

    // ──────────────────────────────────────────
    // POST /admin/project/{id}/save (update)
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postUpdateProject_asAdmin_validData_redirectsToList() throws Exception {
        Project project = new Project("Old Title", "old content", null);
        when(projectService.getById(1L)).thenReturn(project);
        doNothing().when(projectService).saveWithTags(any(), any());

        mockMvc.perform(post("/admin/project/1/save").with(csrf())
                        .param("title", "Updated Title")
                        .param("content", "Updated content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project?saved"));
    }

    // ──────────────────────────────────────────
    // POST /admin/project/delete
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postDeleteProject_asAdmin_redirectsToList() throws Exception {
        doNothing().when(projectService).deleteById(1L);

        mockMvc.perform(post("/admin/project/delete").with(csrf())
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/project?deleted"));

        verify(projectService).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postDeleteProject_withoutCsrf_returns403() throws Exception {
        mockMvc.perform(post("/admin/project/delete")
                        .param("id", "1"))
                .andExpect(status().isForbidden());

        verify(projectService, never()).deleteById(any());
    }

    // ──────────────────────────────────────────
    // Validation — POST /admin/project/save (create)
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveProject_blankTitle_returnsFormWithErrors() throws Exception {
        when(projectService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/project/save").with(csrf())
                        .param("title", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/project-list"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "title"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveProject_titleTooLong_returnsFormWithErrors() throws Exception {
        when(projectService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/project/save").with(csrf())
                        .param("title", "a".repeat(256)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/project-list"))
                .andExpect(model().hasErrors());
    }

    // ──────────────────────────────────────────
    // Validation — POST /admin/project/{id}/save (update)
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postUpdateProject_blankTitle_returnsEditFormWithErrors() throws Exception {
        Project project = new Project("Old Title", "content", null);
        when(projectService.getById(1L)).thenReturn(project);

        mockMvc.perform(post("/admin/project/1/save").with(csrf())
                        .param("title", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/project-edit"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "title"));
    }

    // ──────────────────────────────────────────
    // 確認 AdminProjectController 呼叫 service 傳入正確參數
    // ──────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSaveProject_passesTagsToService() throws Exception {
        mockMvc.perform(post("/admin/project/save").with(csrf())
                        .param("title", "P")
                        .param("tags", "java", "spring"))
                .andExpect(status().is3xxRedirection());

        verify(projectService).createWithTags(eq("P"), any(), any(), any(), eq(List.of("java", "spring")));
    }
}
