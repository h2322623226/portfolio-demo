package com.arauta.portfolio.service;

import com.arauta.portfolio.model.ProjectItem;
import com.arauta.portfolio.repo.ProjectItemRepo;
import com.arauta.portfolio.model.ProjectTag;

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
     */
    @Transactional(readOnly = true)
    public List<ProjectItem> getItemsByPage(String pageName) {
        return projectItemRepo.findByPageName(pageName);
    }

    /**
     * 根據唯一識別碼 (ID) 刪除項目
     */
    @Transactional
    public void deleteItemById(Long id) {
        projectItemRepo.deleteById(id);
    }

    /**
     * 新增 Project 並設定 Tags（統一由此方法負責，避免重複 save）
     */
    @Transactional
    public void createWithTags(String title, String videoUrl, String imageUrl,
                                String content, List<String> tagValues) {
        ProjectItem item = new ProjectItem("projects", "project", title,
                content != null ? content : "", imageUrl);
        item.setVideoUrl(videoUrl);
        saveWithTags(item, tagValues);
    }

    /**
     * 儲存 ProjectItem 並同步 tags（clear + re-insert，最多 5 個）
     */
    @Transactional
    public void saveWithTags(ProjectItem item, List<String> tagValues) {
        item.getTags().clear();
        int order = 0;
        if (tagValues != null) {
            for (String v : tagValues) {
                if (v != null && !v.isBlank() && order < 5) {
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
