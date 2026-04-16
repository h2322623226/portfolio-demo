package com.arauta.portfolio.controller;

import com.arauta.portfolio.dto.SectionForm;
import com.arauta.portfolio.model.Section;
import com.arauta.portfolio.service.SectionService;
import com.arauta.portfolio.service.ContentService;
import com.arauta.portfolio.util.PageNames;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin/homepage")
public class AdminHomepageController {

    private final SectionService sectionService;
    private final ContentService contentService;

    public AdminHomepageController(SectionService sectionService,
                                   ContentService contentService) {
        this.sectionService = sectionService;
        this.contentService = contentService;
    }

    @GetMapping
    public String homepageAdmin(Model model) {
        model.addAttribute("groupedSections",
                sectionService.getGroupedSections(PageNames.HOMEPAGE));
        model.addAttribute("content",
                contentService.getPageContent(PageNames.HOMEPAGE));
        model.addAttribute("groupTypes", Section.GroupType.values());
        return "admin/homepage";
    }

    
    @GetMapping("/section/{id}/edit")
    public String editSection(@PathVariable Long id, Model model) {
        model.addAttribute("section", sectionService.getById(id));
        return "admin/section-edit";
    }

    @PostMapping("/section/create")
    public String createCard(
            @RequestParam String groupKey,
            @RequestParam Section.GroupType groupType) {
        Section created = sectionService.addCard(PageNames.HOMEPAGE, groupKey, groupType);
        return "redirect:/admin/homepage/section/" + created.getId() + "/edit";
    }

    @PostMapping("/rail/save")
    public String saveRail(@RequestParam Map<String, String> allParams) {
        allParams.remove("_csrf");
        contentService.updatePageContent(PageNames.HOMEPAGE, allParams);
        return "redirect:/admin/homepage?saved";
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
        return "redirect:/admin/homepage?saved";
    }

    @PostMapping("/section/{id}/delete")
    public String deleteSection(@PathVariable Long id) {
        sectionService.deleteById(id);
        return "redirect:/admin/homepage?deleted";
    }

}
