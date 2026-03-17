package com.arauta.portfolio.repo;

import com.arauta.portfolio.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SectionRepo extends JpaRepository<Section, Long> {
    List<Section> findByPageNameOrderBySortOrderAsc(String pageName);
    List<Section> findByPageNameAndGroupKeyOrderBySortOrderAsc(String pageName, String groupKey);
    Section findFirstByPageNameOrderBySortOrderDesc(String pageName);
}
