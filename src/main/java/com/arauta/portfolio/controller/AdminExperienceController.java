package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.SectionItem;
import com.arauta.portfolio.service.SectionItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/experience")
public class AdminExperienceController {

    private final SectionItemService sectionService;

    public AdminExperienceController(SectionItemService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping
    public String experienceAdmin(Model model) {
        model.addAttribute("experienceList",
                sectionService.getFlatList("experience"));
        return "admin/experience";
    }

    @PostMapping("/create")
    public String createItem(
            @RequestParam String year,
            @RequestParam String title,
            @RequestParam(required = false) String body) {

        SectionItem item = sectionService.addCard("experience", "timeline", SectionItem.GroupType.TIMELINE);
        item.setYear(year);
        item.setTitle(title);
        item.setBody(body);
        sectionService.saveWithTags(item, null);
        return "redirect:/admin/experience?saved";
    }

    @GetMapping("/{id}/edit")
    public String editItem(@PathVariable Long id, Model model) {
        model.addAttribute("item", sectionService.getById(id));
        return "admin/experience-edit";
    }

    @PostMapping("/{id}/save")
    public String saveItem(
            @PathVariable Long id,
            @RequestParam String year,
            @RequestParam String title,
            @RequestParam(required = false) String body) {

        SectionItem item = sectionService.getById(id);
        item.setYear(year);
        item.setTitle(title);
        item.setBody(body);
        sectionService.saveWithTags(item, null);
        return "redirect:/admin/experience?saved";
    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id) {
        sectionService.deleteById(id);
        return "redirect:/admin/experience?deleted";
    }
}
