package com.example.securitydemo.service;

import com.example.securitydemo.model.ContentBlock;
import com.example.securitydemo.repo.ContentRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 網頁內容管理服務
 * 負責處理 ContentBlock 實體與前端 Map 結構之間的數據轉換與持久化邏輯。
 */
@Service
public class ContentService {

    private final ContentRepo repo;

    public ContentService(ContentRepo repo) {
        this.repo = repo;
    }

    /**
     * 獲取指定頁面的內容映射表
     * 採用 LinkedHashMap 保持數據庫檢索排序。
     */
    @Transactional(readOnly = true)
    public Map<String, String> getPageContent(String pageName) {
        return repo.findByPageNameOrderByKeyAsc(pageName).stream()
                .collect(Collectors.toMap(
                    ContentBlock::getKey, 
                    ContentBlock::getContent,
                    (existing, replacement) -> existing, 
                    LinkedHashMap::new
                ));
    }

    /**
     * 獲取全站頁面分組內容
     * 使用單次全表掃描並在內存中進行分組，優化大規模數據查詢效能。
     */
    @Transactional(readOnly = true)
    public Map<String, Map<String, String>> getAllPagesGroupedByPageName() {
        Map<String, Map<String, String>> result = new TreeMap<>();
        
        repo.findAll().forEach(block -> {
            result.computeIfAbsent(block.getPageName(), k -> new LinkedHashMap<>())
                  .put(block.getKey(), block.getContent());
        });
        
        return result;
    }

    /**
     * 執行內容的 Upsert (更新或新增) 操作
     * 根據 pageName 與 key 進行唯一性檢查，實現冪等性更新。
     */
    @Transactional
    public void upsert(String pageName, String key, String content) {
        ContentBlock block = repo.findByPageNameAndKey(pageName, key)
                .orElseGet(() -> new ContentBlock(pageName, key, ""));
        
        block.setContent(content);
        repo.save(block);
    }

    /**
     * 批量更新指定頁面的內容
     * 遍歷內容映射表並委託 upsert 方法執行持久化。
     */
    @Transactional
    public void updatePageContent(String pageName, Map<String, String> contentMap) {
        contentMap.forEach((key, value) -> {
            this.upsert(pageName, key, value);
        });
    }
}
