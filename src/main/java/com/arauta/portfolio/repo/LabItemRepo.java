package com.arauta.portfolio.repo;

import com.arauta.portfolio.model.LabItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LabItemRepo extends JpaRepository<LabItem, Long> {
    List<LabItem> findAllByOrderBySortOrderAsc();

    /** 取得 sortOrder 最大的那筆，用於計算新項目的排序值（避免全表掃描） */
    Optional<LabItem> findTopByOrderBySortOrderDesc();
}
