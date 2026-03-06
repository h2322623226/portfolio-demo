package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.SectionItem;
import com.arauta.portfolio.service.SectionItemService;
import com.arauta.portfolio.service.ContentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/homepage")
public class AdminSectionController {

    private final SectionItemService sectionService;
    private final ContentService contentService;

    public AdminSectionController(SectionItemService sectionService,
                                   ContentService contentService) {
        this.sectionService = sectionService;
        this.contentService = contentService;
    }

    @GetMapping
    public String homepageAdmin(Model model) {
        model.addAttribute("groupedSections",
                sectionService.getGroupedSections("homepage"));
        model.addAttribute("content",
                contentService.getPageContent("homepage"));
        model.addAttribute("groupTypes", SectionItem.GroupType.values());
        return "admin/homepage";               // 原: "management"
    }

    @PostMapping("/section/create")
    public String createCard(
            @RequestParam String groupKey,
            @RequestParam SectionItem.GroupType groupType) {
        SectionItem created = sectionService.addCard("homepage", groupKey, groupType);
        return "redirect:/admin/homepage/section/" + created.getId() + "/edit";
    }

    @GetMapping("/section/{id}/edit")
    public String editSection(@PathVariable Long id, Model model) {
        model.addAttribute("section", sectionService.getById(id));
        return "admin/section-edit";           
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
        return "redirect:/admin/homepage?saved";
    }

    @PostMapping("/section/{id}/delete")
    public String deleteSection(@PathVariable Long id) {
        sectionService.deleteById(id);
        return "redirect:/admin/homepage?deleted";
    }

    @PostMapping("/rail/save")
    public String saveRail(@RequestParam Map<String, String> allParams) {
        allParams.remove("_csrf");
        contentService.updatePageContent("homepage", allParams);
        return "redirect:/admin/homepage?saved";
    }
}
