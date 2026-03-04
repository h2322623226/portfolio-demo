package com.example.securitydemo.repo;

import com.example.securitydemo.model.LabItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabItemRepo extends JpaRepository<LabItem, Long> {
    List<LabItem> findAllByOrderBySortOrderAsc();
}
