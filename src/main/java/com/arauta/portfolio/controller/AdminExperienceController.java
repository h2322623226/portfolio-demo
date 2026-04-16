package com.arauta.portfolio.controller;

import com.arauta.portfolio.dto.ExperienceForm;
import com.arauta.portfolio.model.Experience;
import com.arauta.portfolio.service.ExperienceService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
            @Valid @ModelAttribute("form") ExperienceForm form,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("experienceList", experienceService.getAll());
            return "admin/experience";
        }
        Experience item = new Experience(form.getYear(), form.getTitle(), form.getBody(), experienceService.nextSortOrder());
        experienceService.save(item);
        return "redirect:/admin/experience?saved";
    }

    @PostMapping("/{id}/save")
    public String saveItem(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") ExperienceForm form,
            BindingResult bindingResult,
            Model model) {
        Experience item = experienceService.getById(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("item", item);
            return "admin/experience-edit";
        }
        item.setYear(form.getYear());
        item.setTitle(form.getTitle());
        item.setBody(form.getBody());
        experienceService.save(item);
        return "redirect:/admin/experience?saved";
    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id) {
        experienceService.deleteById(id);
        return "redirect:/admin/experience?deleted";
    }
}
