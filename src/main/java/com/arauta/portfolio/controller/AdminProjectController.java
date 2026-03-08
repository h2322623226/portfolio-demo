package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.ProjectItem;
import com.arauta.portfolio.service.ProjectItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin/project")
public class AdminProjectController {

    private final ProjectItemService projectItemService;

    public AdminProjectController(ProjectItemService projectItemService) {
        this.projectItemService = projectItemService;
    }

    @GetMapping
    public String adminProjectPage(Model model) {
        model.addAttribute("projectItems", projectItemService.getItemsByPage("projects"));
        return "admin/project-list";
    }

    @GetMapping("/{id}/edit")
    public String editProject(@PathVariable Long id, Model model) {
        model.addAttribute("item", projectItemService.getById(id));
        return "admin/project-edit";
    }

    /**
     * 新增 Project（使用 @RequestParam 明確控制允許的欄位，避免 Mass Assignment 風險）
     */
    @PostMapping("/save")
    public String saveProject(
            @RequestParam String title,
            @RequestParam(required = false) String videoUrl,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<String> tags) {
        projectItemService.createWithTags(title, videoUrl, imageUrl, content, tags);
        return "redirect:/admin/project?saved";
    }

    /**
     * 更新 Project
     */
    @PostMapping("/{id}/save")
    public String updateProject(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String videoUrl,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<String> tags) {
        ProjectItem item = projectItemService.getById(id);
        item.setTitle(title);
        item.setVideoUrl(videoUrl);
        item.setImageUrl(imageUrl);
        item.setContent(content);
        projectItemService.saveWithTags(item, tags);
        return "redirect:/admin/project?saved";
    }

    @PostMapping("/delete")
    public String deleteProject(@RequestParam("id") Long id) {
        projectItemService.deleteItemById(id);
        return "redirect:/admin/project?deleted";
    }
}
