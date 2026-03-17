package com.arauta.portfolio.controller;

import com.arauta.portfolio.service.ContentService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

/**
 * [Drawer 資料自動注入]
 * 限定作用於 HomeController，讓所有公開頁面自動取得 drawer 所需的 rail.* 內容，
 * 無需在每個 Controller 方法中手動呼叫 contentService。
 */
@ControllerAdvice(assignableTypes = HomeController.class)
public class DrawerControllerAdvice {

    private final ContentService contentService;

    public DrawerControllerAdvice(ContentService contentService) {
        this.contentService = contentService;
    }

    @ModelAttribute("drawerContent")
    public Map<String, String> drawerContent() {
        return contentService.getDrawerContent();
    }
}
