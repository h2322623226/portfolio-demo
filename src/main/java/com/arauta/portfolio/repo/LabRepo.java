package com.arauta.portfolio.repo;

import com.arauta.portfolio.model.LabEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabRepo extends JpaRepository<LabEntry, Long> {
    List<LabEntry> findAllByOrderBySortOrderAsc();

    /** 取得 sortOrder 最大的那筆，用於計算新項目的排序值 */
    Optional<LabEntry> findTopByOrderBySortOrderDesc();
}
