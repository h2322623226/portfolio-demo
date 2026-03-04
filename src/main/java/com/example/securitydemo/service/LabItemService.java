package com.example.securitydemo.service;

import com.example.securitydemo.model.LabItem;
import com.example.securitydemo.model.LabTag;
import com.example.securitydemo.repo.LabItemRepo;
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

    @Transactional
    public LabItem create(String name) {
        List<LabItem> all = repo.findAllByOrderBySortOrderAsc();
        int next = all.isEmpty() ? 0 : all.get(all.size() - 1).getSortOrder() + 1;
        LabItem item = new LabItem(name);
        item.setSortOrder(next);
        return repo.save(item);
    }

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
