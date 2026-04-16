package com.arauta.portfolio.controller;

import com.arauta.portfolio.dto.LabForm;
import com.arauta.portfolio.model.LabEntry;
import com.arauta.portfolio.service.LabService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/lab")
public class AdminLabController {

    private final LabService labService;

    public AdminLabController(LabService labService) {
        this.labService = labService;
    }

    @GetMapping
    public String labAdmin(Model model) {
        model.addAttribute("labEntries", labService.getAll());
        return "admin/lab";
    }

    @GetMapping("/{id}/edit")
    public String editItem(@PathVariable Long id, Model model) {
        model.addAttribute("item", labService.getById(id));
        return "admin/lab-edit";
    }

    @PostMapping("/save")
    public String saveItem(
            @Valid @ModelAttribute("form") LabForm form,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("labEntries", labService.getAll());
            return "admin/lab";
        }
        LabEntry item = labService.prepareNew(form.getName(), form.getDescription(), form.getLinkUrl(), form.getImageUrl());
        labService.saveWithTags(item, form.getTags());
        return "redirect:/admin/lab?saved";
    }

    @PostMapping("/{id}/save")
    public String updateItem(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") LabForm form,
            BindingResult bindingResult,
            Model model) {
        LabEntry item = labService.getById(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("item", item);
            return "admin/lab-edit";
        }
        item.setName(form.getName());
        item.setDescription(form.getDescription());
        item.setLinkUrl(form.getLinkUrl());
        item.setImageUrl(form.getImageUrl());
        labService.saveWithTags(item, form.getTags());
        return "redirect:/admin/lab?saved";
    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id) {
        labService.deleteById(id);
        return "redirect:/admin/lab?deleted";
    }
}
