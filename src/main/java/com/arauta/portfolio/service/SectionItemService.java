package com.arauta.portfolio.service;

import com.arauta.portfolio.model.SectionItem;
import com.arauta.portfolio.model.SectionTag;
import com.arauta.portfolio.repo.SectionItemRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SectionItemService {

    private final SectionItemRepo repo;

    public SectionItemService(SectionItemRepo repo) {
        this.repo = repo;
    }
    
    public List<SectionItem> getFlatList(String pageName) {
        return repo.findByPageNameOrderBySortOrderAsc(pageName);
    }
    /**
     * 取得某頁面所有卡片，並依 groupKey 分組。
     * 回傳 LinkedHashMap 保持 sortOrder 順序，
     * key = groupKey，value = 同 group 的卡片清單。
     */
    @Transactional(readOnly = true)
    public LinkedHashMap<String, List<SectionItem>> getGroupedSections(String pageName) {
        List<SectionItem> all = repo.findByPageNameOrderBySortOrderAsc(pageName);
        LinkedHashMap<String, List<SectionItem>> grouped = new LinkedHashMap<>();
        for (SectionItem item : all) {
            grouped.computeIfAbsent(item.getGroupKey(), k -> new ArrayList<>()).add(item);
        }
        return grouped;
    }

    @Transactional(readOnly = true)
    public SectionItem getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Section not found: " + id));
    }

    /**
     * 新增一張卡片到指定 group（或建立新 group）
     * 若 groupKey 已存在，sortOrder 接在該 group 最後一筆之後
     */
    @Transactional
    public SectionItem addCard(String pageName, String groupKey,
                                SectionItem.GroupType groupType) {
        SectionItem last = repo.findFirstByPageNameOrderBySortOrderDesc(pageName);
        int next = (last == null) ? 0 : last.getSortOrder() + 1;
        return repo.save(new SectionItem(pageName, groupKey, groupType, next));
    }

    /**
     * 儲存卡片內容 + 同步 tags（clear + re-insert，最多5個）
     */
    @Transactional
    public void saveWithTags(SectionItem item, List<String> tagValues) {
        item.getTags().clear();
        int order = 0;
        if (tagValues != null) {
            for (String v : tagValues) {
                if (v != null && !v.isBlank() && order < 5) {
                    item.getTags().add(new SectionTag(item, v.trim(), order++));
                }
            }
        }
        repo.save(item);
    }

    @Transactional
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}