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
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * DrawerControllerAdvice 的行為測試。
 * Advice 僅對 HomeController 生效（assignableTypes = HomeController.class），
 * 所以用 @WebMvcTest(HomeController.class) 來觸發它。
 */
@WebMvcTest(HomeController.class)
class DrawerControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

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
    // Drawer 資料注入
    // ──────────────────────────────────────────

    @Test
    @WithMockUser
    void drawerContent_isInjectedIntoEveryPublicPage() throws Exception {
        Map<String, String> drawerData = Map.of("rail.name", "Alice", "rail.bio", "Developer");
        when(contentService.getDrawerContent()).thenReturn(new LinkedHashMap<>(drawerData));
        when(contentService.getPageContent(anyString())).thenReturn(new LinkedHashMap<>());
        when(sectionService.getGroupedSections(anyString())).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/public/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("drawerContent"));
    }

    @Test
    @WithMockUser
    void drawerContent_isInjectedOnProjectsPage() throws Exception {
        when(contentService.getDrawerContent()).thenReturn(new LinkedHashMap<>());
        when(projectService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/public/projects"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("drawerContent"));
    }

    @Test
    @WithMockUser
    void drawerContent_isInjectedOnExperiencePage() throws Exception {
        when(contentService.getDrawerContent()).thenReturn(new LinkedHashMap<>());
        when(experienceService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/public/experience"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("drawerContent"));
    }

    @Test
    @WithMockUser
    void drawerContent_emptyMap_pageStillRenders() throws Exception {
        // Drawer 資料為空時，頁面應正常渲染（不拋 NPE）
        when(contentService.getDrawerContent()).thenReturn(new LinkedHashMap<>());
        when(projectService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/public/projects"))
                .andExpect(status().isOk());
    }
}
