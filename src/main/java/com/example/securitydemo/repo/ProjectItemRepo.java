package com.example.securitydemo.repo;

import com.example.securitydemo.model.ProjectItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 專案項目資料存取層
 * 繼承 JpaRepository 以獲取標準 CRUD 功能，支援專案項目的動態增減與檢索。
 */
@Repository
public interface ProjectItemRepo extends JpaRepository<ProjectItem, Long> {
    List<ProjectItem> findByPageName(String pageName);
    List<ProjectItem> findByPageNameAndKey(String pageName, String key);
}
