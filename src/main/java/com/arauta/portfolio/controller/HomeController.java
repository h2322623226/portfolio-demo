package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.ProjectItem;
import com.arauta.portfolio.service.ContentService;
import com.arauta.portfolio.service.ProjectItemService;
import com.arauta.portfolio.service.SectionItemService;
import com.arauta.portfolio.service.LabItemService;
import com.arauta.portfolio.model.SectionItem;

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
    private final ProjectItemService projectItemService;
    private final SectionItemService sectionItemService;
    private final LabItemService labItemService;

    public HomeController(
        ContentService contentService,
        ProjectItemService projectItemService,
        SectionItemService sectionItemService,
        LabItemService labItemService) {
        this.contentService = contentService;
        this.projectItemService = projectItemService;
        this.sectionItemService = sectionItemService;
        this.labItemService = labItemService;
    }

    @GetMapping("/")
    public String homepage(Model model) {
        model.addAttribute("content", contentService.getPageContent("homepage"));
        model.addAttribute("groupedSections", sectionItemService.getGroupedSections("homepage"));
        return "public/index";                 
    }

    @GetMapping("/projects")
    public String projectsPage(Model model) {
        model.addAttribute("content", contentService.getPageContent("homepage"));
        List<ProjectItem> projectList = projectItemService.getItemsByPage("projects");
        model.addAttribute("projectList", projectList);
        return "public/projects";
    }
    
    @GetMapping("/skills")
    public String skills(Model model) {
        model.addAttribute("content", contentService.getPageContent("homepage"));
        model.addAttribute("groupedSections", sectionItemService.getGroupedSections("skills"));
        return "public/skills";
    }

    @GetMapping("/experience")
    public String experience(Model model) {
        model.addAttribute("content", contentService.getPageContent("homepage"));
        List<SectionItem> list = sectionItemService.getFlatList("experience");
        java.util.Collections.reverse(list);
        model.addAttribute("experienceList", list);
        return "public/experience";
    }

    @GetMapping("/lab")
    public String lab(Model model) {
        model.addAttribute("content", contentService.getPageContent("homepage"));
        model.addAttribute("labItems", labItemService.getAll());
        return "public/lab";
    }
}
