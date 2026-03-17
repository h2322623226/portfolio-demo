package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.Project;
import com.arauta.portfolio.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin/project")
public class AdminProjectController {

    private final ProjectService projectService;

    public AdminProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String adminProjectPage(Model model) {
        model.addAttribute("projects", projectService.getAll());
        return "admin/project-list";
    }

    @GetMapping("/{id}/edit")
    public String editProject(@PathVariable Long id, Model model) {
        model.addAttribute("item", projectService.getById(id));
        return "admin/project-edit";
    }

    @PostMapping("/save")
    public String saveProject(
            @RequestParam String title,
            @RequestParam(required = false) String videoUrl,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<String> tags) {
        projectService.createWithTags(title, videoUrl, imageUrl, content, tags);
        return "redirect:/admin/project?saved";
    }

    @PostMapping("/{id}/save")
    public String updateProject(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String videoUrl,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<String> tags) {
        Project item = projectService.getById(id);
        item.setTitle(title);
        item.setVideoUrl(videoUrl);
        item.setImageUrl(imageUrl);
        item.setContent(content);
        projectService.saveWithTags(item, tags);
        return "redirect:/admin/project?saved";
    }

    @PostMapping("/delete")
    public String deleteProject(@RequestParam("id") Long id) {
        projectService.deleteById(id);
        return "redirect:/admin/project?deleted";
    }
}
