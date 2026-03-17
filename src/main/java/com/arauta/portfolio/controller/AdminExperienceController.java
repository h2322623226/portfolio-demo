package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.Experience;
import com.arauta.portfolio.service.ExperienceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/experience")
public class AdminExperienceController {

    private final ExperienceService experienceService;

    public AdminExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @GetMapping
    public String experienceAdmin(Model model) {
        model.addAttribute("experienceList", experienceService.getAll());
        return "admin/experience";
    }

    @GetMapping("/{id}/edit")
    public String editItem(@PathVariable Long id, Model model) {
        model.addAttribute("item", experienceService.getById(id));
        return "admin/experience-edit";
    }

    @PostMapping("/create")
    public String createItem(
            @RequestParam String year,
            @RequestParam String title,
            @RequestParam(required = false) String body) {

        Experience item = new Experience(year, title, body, experienceService.nextSortOrder());
        experienceService.save(item);
        return "redirect:/admin/experience?saved";
    }

    @PostMapping("/{id}/save")
    public String saveItem(
            @PathVariable Long id,
            @RequestParam String year,
            @RequestParam String title,
            @RequestParam(required = false) String body) {

        Experience item = experienceService.getById(id);
        item.setYear(year);
        item.setTitle(title);
        item.setBody(body);
        experienceService.save(item);
        return "redirect:/admin/experience?saved";
    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id) {
        experienceService.deleteById(id);
        return "redirect:/admin/experience?deleted";
    }
}
