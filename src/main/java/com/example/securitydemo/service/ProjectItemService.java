package com.example.securitydemo.service;

import com.example.securitydemo.model.ProjectItem;
import com.example.securitydemo.repo.ProjectItemRepo;

import com.example.securitydemo.model.ProjectTag;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 專案項目業務邏輯服務
 * 負責處理 ProjectItem 實體的 CRUD 操作，支援前端動態項目的渲染與後台管理。
 */
@Service
public class ProjectItemService {

    private final ProjectItemRepo projectItemRepo;

    public ProjectItemService(ProjectItemRepo projectItemRepo) {
        this.projectItemRepo = projectItemRepo;
    }

    /**
     * 獲取指定頁面的所有項目清單
     * 用於前端 th:each 迭代渲染。
     */
    @Transactional(readOnly = true)
    public List<ProjectItem> getItemsByPage(String pageName) {
        return projectItemRepo.findByPageName(pageName);
    }

    /**
     * 持久化單一項目（新增或更新）
     * 若實體包含 ID 則執行更新，否則執行新增。
     */
    @Transactional
    public void saveItem(ProjectItem item) {
        projectItemRepo.save(item);
    }

    /**
     * 根據唯一識別碼 (ID) 刪除項目
     */
    @Transactional
    public void deleteItemById(Long id) {
        projectItemRepo.deleteById(id);
    }

    @Transactional
    public void saveWithTags(ProjectItem item, List<String> tagValues) {
        item.getTags().clear();
        int order = 0;
        if (tagValues != null) {
            for (String v : tagValues) {
                if (v != null && !v.isBlank() && order < 3) {
                    item.getTags().add(new ProjectTag(item, v.trim(), order++));
                }
            }
        }
        projectItemRepo.save(item);
    }

    @Transactional(readOnly = true)
    public ProjectItem getById(Long id) {
        return projectItemRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }
}
