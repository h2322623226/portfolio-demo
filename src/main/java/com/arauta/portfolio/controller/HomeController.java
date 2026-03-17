package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.Experience;
import com.arauta.portfolio.model.Project;
import com.arauta.portfolio.service.ContentService;
import com.arauta.portfolio.service.ExperienceService;
import com.arauta.portfolio.service.ProjectService;
import com.arauta.portfolio.service.SectionService;
import com.arauta.portfolio.service.LabService;
import com.arauta.portfolio.util.PageNames;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 前台核心控制器
 * 負責處理全站公共路由請求，並負責動態頁面內容的加載與分發。
 */
@Controller
@RequestMapping("/public")
public class HomeController {

    private final ContentService contentService;
    private final ProjectService projectService;
    private final SectionService sectionService;
    private final LabService labService;
    private final ExperienceService experienceService;

    public HomeController(
        ContentService contentService,
        ProjectService projectService,
        SectionService sectionService,
        LabService labService,
        ExperienceService experienceService) {
        this.contentService = contentService;
        this.projectService = projectService;
        this.sectionService = sectionService;
        this.labService = labService;
        this.experienceService = experienceService;
    }

    @GetMapping("/")
    public String homepage(Model model) {
        model.addAttribute("content", contentService.getPageContent(PageNames.HOMEPAGE));
        model.addAttribute("groupedSections", sectionService.getGroupedSections(PageNames.HOMEPAGE));
        return "public/index";
    }

    @GetMapping("/projects")
    public String projectsPage(Model model) {
        List<Project> projects = projectService.getAll();
        model.addAttribute("projects", projects);
        return "public/projects";
    }

    @GetMapping("/skills")
    public String skills(Model model) {
        model.addAttribute("groupedSections", sectionService.getGroupedSections(PageNames.SKILLS));
        return "public/skills";
    }

    @GetMapping("/experience")
    public String experience(Model model) {
        List<Experience> list = experienceService.getAll();
        java.util.Collections.reverse(list);
        model.addAttribute("experienceList", list);
        return "public/experience";
    }

    @GetMapping("/lab")
    public String lab(Model model) {
        model.addAttribute("labEntries", labService.getAll());
        return "public/lab";
    }
}
