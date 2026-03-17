package com.arauta.portfolio.service;

import com.arauta.portfolio.model.Experience;
import com.arauta.portfolio.repo.ExperienceRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExperienceService {

    private final ExperienceRepo repo;

    public ExperienceService(ExperienceRepo repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Experience> getAll() {
        return repo.findAllByOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public Experience getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found: " + id));
    }

    @Transactional
    public Experience save(Experience item) {
        return repo.save(item);
    }

    @Transactional
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public int nextSortOrder() {
        return repo.findTopByOrderBySortOrderDesc()
                .map(last -> last.getSortOrder() + 1)
                .orElse(0);
    }
}
