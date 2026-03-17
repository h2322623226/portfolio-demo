package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.LabEntry;
import com.arauta.portfolio.service.LabService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String linkUrl,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) List<String> tags) {

        LabEntry item = labService.prepareNew(name, description, linkUrl, imageUrl);
        labService.saveWithTags(item, tags);
        return "redirect:/admin/lab?saved";
    }

    @PostMapping("/{id}/save")
    public String updateItem(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String linkUrl,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) List<String> tags) {

        LabEntry item = labService.getById(id);
        item.setName(name);
        item.setDescription(description);
        item.setLinkUrl(linkUrl);
        item.setImageUrl(imageUrl);
        labService.saveWithTags(item, tags);
        return "redirect:/admin/lab?saved";
    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id) {
        labService.deleteById(id);
        return "redirect:/admin/lab?deleted";
    }
}
