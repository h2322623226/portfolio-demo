package com.arauta.portfolio.repo;

import com.arauta.portfolio.model.SectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SectionItemRepo extends JpaRepository<SectionItem, Long> {
    List<SectionItem> findByPageNameOrderBySortOrderAsc(String pageName);
    List<SectionItem> findByPageNameAndGroupKeyOrderBySortOrderAsc(String pageName, String groupKey);
    SectionItem findFirstByPageNameOrderBySortOrderDesc(String pageName);
}