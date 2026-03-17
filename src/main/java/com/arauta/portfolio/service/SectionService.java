package com.arauta.portfolio.service;

import com.arauta.portfolio.model.Section;
import com.arauta.portfolio.model.SectionTag;
import com.arauta.portfolio.repo.SectionRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SectionService {

    private static final int MAX_TAGS = 5;

    private final SectionRepo repo;

    public SectionService(SectionRepo repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public LinkedHashMap<String, List<Section>> getGroupedSections(String pageName) {
        List<Section> all = repo.findByPageNameOrderBySortOrderAsc(pageName);
        LinkedHashMap<String, List<Section>> grouped = new LinkedHashMap<>();
        for (Section item : all) {
            grouped.computeIfAbsent(item.getGroupKey(), k -> new ArrayList<>()).add(item);
        }
        return grouped;
    }

    @Transactional(readOnly = true)
    public Section getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Section not found: " + id));
    }

    @Transactional
    public Section addCard(String pageName, String groupKey,
                            Section.GroupType groupType) {
        Section last = repo.findFirstByPageNameOrderBySortOrderDesc(pageName);
        int next = (last == null) ? 0 : last.getSortOrder() + 1;
        return repo.save(new Section(pageName, groupKey, groupType, next));
    }

    @Transactional
    public void saveWithTags(Section item, List<String> tagValues) {
        item.getTags().clear();
        int order = 0;
        if (tagValues != null) {
            for (String v : tagValues) {
                if (v != null && !v.isBlank() && order < MAX_TAGS) {
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
