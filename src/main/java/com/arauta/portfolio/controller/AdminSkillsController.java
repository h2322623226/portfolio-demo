package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.SectionItem;
import com.arauta.portfolio.service.SectionItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/skills")
public class AdminSkillsController {

    private final SectionItemService sectionService;

    public AdminSkillsController(SectionItemService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping
    public String skillsAdmin(Model model) {
        model.addAttribute("groupedSections",
                sectionService.getGroupedSections("skills"));
        model.addAttribute("groupTypes", SectionItem.GroupType.values());
        return "admin/skills";
    }

    @PostMapping("/section/create")
    public String createCard(
            @RequestParam String groupKey,
            @RequestParam SectionItem.GroupType groupType) {
        SectionItem created = sectionService.addCard("skills", groupKey, groupType);
        return "redirect:/admin/skills/section/" + created.getId() + "/edit";
    }

    @GetMapping("/section/{id}/edit")
    public String editSection(@PathVariable Long id, Model model) {
        model.addAttribute("section", sectionService.getById(id));
        return "admin/section-edit";           // 共用 section-edit 頁
    }

    @PostMapping("/section/{id}/save")
    public String saveSection(
            @PathVariable Long id,
            @RequestParam(required = false) String sectionLabel,
            @RequestParam(required = false) String groupKey,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String subtitle,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String body,
            @RequestParam(required = false) List<String> tags) {

        SectionItem item = sectionService.getById(id);
        item.setSectionLabel(sectionLabel);
        if (groupKey != null && !groupKey.isBlank()) item.setGroupKey(groupKey);
        item.setTitle(title);
        item.setSubtitle(subtitle);
        item.setYear(year);
        item.setBody(body);
        sectionService.saveWithTags(item, tags);
        return "redirect:/admin/skills?saved";
    }

    @PostMapping("/section/{id}/delete")
    public String deleteSection(@PathVariable Long id) {
        sectionService.deleteById(id);
        return "redirect:/admin/skills?deleted";
    }
}
