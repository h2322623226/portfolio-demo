package com.arauta.portfolio.service;

import com.arauta.portfolio.model.LabEntry;
import com.arauta.portfolio.model.LabTag;
import com.arauta.portfolio.repo.LabRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LabService {

    private static final int MAX_TAGS = 5;

    private final LabRepo repo;

    public LabService(LabRepo repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<LabEntry> getAll() {
        return repo.findAllByOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public LabEntry getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("LabEntry not found: " + id));
    }

    @Transactional(readOnly = true)
    public LabEntry prepareNew(String name, String description, String linkUrl, String imageUrl) {
        int next = repo.findTopByOrderBySortOrderDesc()
                .map(last -> last.getSortOrder() + 1)
                .orElse(0);
        LabEntry item = new LabEntry(name);
        item.setSortOrder(next);
        item.setDescription(description);
        item.setLinkUrl(linkUrl);
        item.setImageUrl(imageUrl);
        return item;
    }

    @Transactional
    public void saveWithTags(LabEntry item, List<String> tagValues) {
        item.getTags().clear();
        int order = 0;
        if (tagValues != null) {
            for (String v : tagValues) {
                if (v != null && !v.isBlank() && order < MAX_TAGS) {
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
