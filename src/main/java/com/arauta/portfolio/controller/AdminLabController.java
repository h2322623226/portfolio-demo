package com.arauta.portfolio.controller;

import com.arauta.portfolio.model.LabItem;
import com.arauta.portfolio.service.LabItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/lab")
public class AdminLabController {

    private final LabItemService labItemService;

    public AdminLabController(LabItemService labItemService) {
        this.labItemService = labItemService;
    }

    @GetMapping
    public String labAdmin(Model model) {
        model.addAttribute("labItems", labItemService.getAll());
        return "admin/lab";
    }

    @GetMapping("/{id}/edit")
    public String editItem(@PathVariable Long id, Model model) {
        model.addAttribute("item", labItemService.getById(id));
        return "admin/lab-edit";
    }

    /**
     * 新增 LabItem：先建立並設定所有欄位，再統一呼叫 saveWithTags 一次完成儲存。
     */
    @PostMapping("/save")
    public String saveItem(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String linkUrl,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) List<String> tags) {

        LabItem item = labItemService.prepareNew(name, description, linkUrl, imageUrl);
        labItemService.saveWithTags(item, tags);
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

        LabItem item = labItemService.getById(id);
        item.setName(name);
        item.setDescription(description);
        item.setLinkUrl(linkUrl);
        item.setImageUrl(imageUrl);
        labItemService.saveWithTags(item, tags);
        return "redirect:/admin/lab?saved";
    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id) {
        labItemService.deleteById(id);
        return "redirect:/admin/lab?deleted";
    }
}
