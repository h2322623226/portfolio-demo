package com.arauta.portfolio.controller;

import com.arauta.portfolio.dto.SectionForm;
import com.arauta.portfolio.model.Section;
import com.arauta.portfolio.service.SectionService;
import com.arauta.portfolio.util.PageNames;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/skills")
public class AdminSkillsController {

    private final SectionService sectionService;

    public AdminSkillsController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping
    public String skillsAdmin(Model model) {
        model.addAttribute("groupedSections",
                sectionService.getGroupedSections(PageNames.SKILLS));
        model.addAttribute("groupTypes", Section.GroupType.values());
        return "admin/skills";
    }

    @PostMapping("/section/create")
    public String createCard(
            @RequestParam String groupKey,
            @RequestParam Section.GroupType groupType) {
        Section created = sectionService.addCard(PageNames.SKILLS, groupKey, groupType);
        return "redirect:/admin/skills/section/" + created.getId() + "/edit";
    }

    @GetMapping("/section/{id}/edit")
    public String editSection(@PathVariable Long id, Model model) {
        model.addAttribute("section", sectionService.getById(id));
        return "admin/section-edit";
    }

    @PostMapping("/section/{id}/save")
    public String saveSection(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") SectionForm form,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("section", sectionService.getById(id));
            return "admin/section-edit";
        }
        Section item = sectionService.getById(id);
        item.setSectionLabel(form.getSectionLabel());
        if (form.getGroupKey() != null && !form.getGroupKey().isBlank()) item.setGroupKey(form.getGroupKey());
        item.setTitle(form.getTitle());
        item.setSubtitle(form.getSubtitle());
        item.setBody(form.getBody());
        sectionService.saveWithTags(item, form.getTags());
        return "redirect:/admin/skills?saved";
    }

    @PostMapping("/section/{id}/delete")
    public String deleteSection(@PathVariable Long id) {
        sectionService.deleteById(id);
        return "redirect:/admin/skills?deleted";
    }
}
