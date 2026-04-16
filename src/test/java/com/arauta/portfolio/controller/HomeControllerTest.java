package com.arauta.portfolio.controller;

import com.arauta.portfolio.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * HomeController 的切片測試。
 * DrawerControllerAdvice 僅作用於 HomeController，因此也會被此測試載入。
 * ContentService 需同時滿足 HomeController 與 DrawerControllerAdvice 的依賴。
 */
@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // HomeController 的依賴
    @MockBean
    private ContentService contentService;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private SectionService sectionService;
    @MockBean
    private LabService labService;
    @MockBean
    private ExperienceService experienceService;

    // ──────────────────────────────────────────
    // GET /public/
    // ──────────────────────────────────────────

    @Test
    @WithMockUser
    void getHome_returns200AndIndexView() throws Exception {
        when(contentService.getPageContent(anyString())).thenReturn(new LinkedHashMap<>());
        when(contentService.getDrawerContent()).thenReturn(new LinkedHashMap<>());
        when(sectionService.getGroupedSections(anyString())).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/public/"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/index"))
                .andExpect(model().attributeExists("content", "groupedSections"));
    }

    // ──────────────────────────────────────────
    // GET /public/projects
    // ──────────────────────────────────────────

    @Test
    @WithMockUser
    void getProjects_returns200AndProjectsView() throws Exception {
        when(projectService.getAll()).thenReturn(Collections.emptyList());
        when(contentService.getDrawerContent()).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/public/projects"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/projects"))
                .andExpect(model().attributeExists("projects"));
    }

    @Test
    @WithMockUser
    void getProjects_emptyList_rendersWithoutError() throws Exception {
        when(projectService.getAll()).thenReturn(Collections.emptyList());
        when(contentService.getDrawerContent()).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/public/projects"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("projects", Collections.emptyList()));
    }

    // ──────────────────────────────────────────
    // GET /public/skills
    // ──────────────────────────────────────────

    @Test
    @WithMockUser
    void getSkills_returns200AndSkillsView() throws Exception {
        when(sectionService.getGroupedSections(anyString())).thenReturn(new LinkedHashMap<>());
        when(contentService.getDrawerContent()).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/public/skills"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/skills"))
                .andExpect(model().attributeExists("groupedSections"));
    }

    // ──────────────────────────────────────────
    // GET /public/experience
    // ──────────────────────────────────────────

    @Test
    @WithMockUser
    void getExperience_returns200AndExperienceView() throws Exception {
        when(experienceService.getAll()).thenReturn(Collections.emptyList());
        when(contentService.getDrawerContent()).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/public/experience"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/experience"))
                .andExpect(model().attributeExists("experienceList"));
    }

    @Test
    @WithMockUser
    void getExperience_modelContainsReversedList() throws Exception {
        com.arauta.portfolio.model.Experience e1 = new com.arauta.portfolio.model.Experience("2020", "First", "", 0);
        com.arauta.portfolio.model.Experience e2 = new com.arauta.portfolio.model.Experience("2023", "Second", "", 1);
        when(experienceService.getAll()).thenReturn(new java.util.ArrayList<>(List.of(e1, e2)));
        when(contentService.getDrawerContent()).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/public/experience"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("experienceList",
                        org.hamcrest.Matchers.contains(e2, e1)));
    }

    // ──────────────────────────────────────────
    // GET /public/lab
    // ──────────────────────────────────────────

    @Test
    @WithMockUser
    void getLab_returns200AndLabView() throws Exception {
        when(labService.getAll()).thenReturn(Collections.emptyList());
        when(contentService.getDrawerContent()).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/public/lab"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/lab"))
                .andExpect(model().attributeExists("labEntries"));
    }

    // ──────────────────────────────────────────
    // DrawerControllerAdvice 注入驗證
    // ──────────────────────────────────────────

    @Test
    @WithMockUser
    void drawerContent_isInjectedIntoModel() throws Exception {
        Map<String, String> drawerData = Map.of("rail.title", "Hello");
        when(contentService.getDrawerContent()).thenReturn(new LinkedHashMap<>(drawerData));
        when(contentService.getPageContent(anyString())).thenReturn(new LinkedHashMap<>());
        when(sectionService.getGroupedSections(anyString())).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/public/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("drawerContent"));
    }
}
