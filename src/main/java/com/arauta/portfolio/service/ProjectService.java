package com.arauta.portfolio.service;

import com.arauta.portfolio.model.Project;
import com.arauta.portfolio.repo.ProjectRepo;
import com.arauta.portfolio.model.ProjectTag;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private static final int MAX_TAGS = 5;

    private final ProjectRepo projectRepo;

    public ProjectService(ProjectRepo projectRepo) {
        this.projectRepo = projectRepo;
    }

    @Transactional(readOnly = true)
    public List<Project> getAll() {
        return projectRepo.findAllByOrderByIdAsc();
    }

    @Transactional
    public void deleteById(Long id) {
        projectRepo.deleteById(id);
    }

    @Transactional
    public void createWithTags(String title, String videoUrl, String imageUrl,
                                String content, List<String> tagValues) {
        Project item = new Project(title,
                content != null ? content : "", imageUrl);
        item.setVideoUrl(videoUrl);
        saveWithTags(item, tagValues);
    }

    @Transactional
    public void saveWithTags(Project item, List<String> tagValues) {
        item.getTags().clear();
        int order = 0;
        if (tagValues != null) {
            for (String v : tagValues) {
                if (v != null && !v.isBlank() && order < MAX_TAGS) {
                    item.getTags().add(new ProjectTag(item, v.trim(), order++));
                }
            }
        }
        projectRepo.save(item);
    }

    @Transactional(readOnly = true)
    public Project getById(Long id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }
}
