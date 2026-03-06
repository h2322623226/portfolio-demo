package com.arauta.portfolio.service;

import com.arauta.portfolio.model.LabItem;
import com.arauta.portfolio.model.LabTag;
import com.arauta.portfolio.repo.LabItemRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LabItemService {

    private final LabItemRepo repo;

    public LabItemService(LabItemRepo repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<LabItem> getAll() {
        return repo.findAllByOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public LabItem getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("LabItem not found: " + id));
    }

    /**
     * 準備一個新的 LabItem（設定 sortOrder 與欄位），但不儲存。
     * 由 Controller 呼叫 saveWithTags() 統一完成儲存，避免重複 save。
     */
    @Transactional(readOnly = true)
    public LabItem prepareNew(String name, String description, String linkUrl, String imageUrl) {
        int next = repo.findTopByOrderBySortOrderDesc()
                .map(last -> last.getSortOrder() + 1)
                .orElse(0);
        LabItem item = new LabItem(name);
        item.setSortOrder(next);
        item.setDescription(description);
        item.setLinkUrl(linkUrl);
        item.setImageUrl(imageUrl);
        return item;
    }

    /**
     * 儲存 LabItem 並同步 tags（clear + re-insert，最多 3 個）
     */
    @Transactional
    public void saveWithTags(LabItem item, List<String> tagValues) {
        item.getTags().clear();
        int order = 0;
        if (tagValues != null) {
            for (String v : tagValues) {
                if (v != null && !v.isBlank() && order < 3) {
                    item.getTags().add(new LabTag(item, v.trim(), order++));
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
