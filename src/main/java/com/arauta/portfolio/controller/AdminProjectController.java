package com.arauta.portfolio.controller;

import com.arauta.portfolio.dto.ProjectForm;
import com.arauta.portfolio.model.Project;
import com.arauta.portfolio.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
            @Valid @ModelAttribute("form") ProjectForm form,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("projects", projectService.getAll());
            return "admin/project-list";
        }
        projectService.createWithTags(form.getTitle(), form.getVideoUrl(), form.getImageUrl(), form.getContent(), form.getTags());
        return "redirect:/admin/project?saved";
    }

    @PostMapping("/{id}/save")
    public String updateProject(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") ProjectForm form,
            BindingResult bindingResult,
            Model model) {
        Project item = projectService.getById(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("item", item);
            return "admin/project-edit";
        }
        item.setTitle(form.getTitle());
        item.setVideoUrl(form.getVideoUrl());
        item.setImageUrl(form.getImageUrl());
        item.setContent(form.getContent());
        projectService.saveWithTags(item, form.getTags());
        return "redirect:/admin/project?saved";
    }

    @PostMapping("/delete")
    public String deleteProject(@RequestParam("id") Long id) {
        projectService.deleteById(id);
        return "redirect:/admin/project?deleted";
    }
}
