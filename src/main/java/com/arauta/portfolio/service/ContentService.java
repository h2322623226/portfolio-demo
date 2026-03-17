package com.arauta.portfolio.service;

import com.arauta.portfolio.model.ContentBlock;
import com.arauta.portfolio.repo.ContentRepo;
import com.arauta.portfolio.util.PageNames;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ContentService {

    private final ContentRepo repo;

    public ContentService(ContentRepo repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Map<String, String> getDrawerContent() {
        return repo.findByPageNameAndKeyStartingWith(PageNames.HOMEPAGE, "rail.")
                .stream()
                .collect(Collectors.toMap(
                        ContentBlock::getKey,
                        ContentBlock::getContent,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

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

    @Transactional(readOnly = true)
    public Map<String, Map<String, String>> getAllPagesGroupedByPageName() {
        Map<String, Map<String, String>> result = new TreeMap<>();
        
        repo.findAll().forEach(block -> {
            result.computeIfAbsent(block.getPageName(), k -> new LinkedHashMap<>())
                  .put(block.getKey(), block.getContent());
        });
        
        return result;
    }

    @Transactional
    public void upsert(String pageName, String key, String content) {
        ContentBlock block = repo.findByPageNameAndKey(pageName, key)
                .orElseGet(() -> new ContentBlock(pageName, key, ""));
        
        block.setContent(content);
        repo.save(block);
    }

    @Transactional
    public void updatePageContent(String pageName, Map<String, String> contentMap) {
        contentMap.forEach((key, value) -> {
            this.upsert(pageName, key, value);
        });
    }
}
