package com.arauta.portfolio.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.arauta.portfolio.model.ContentBlock;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * [內容區塊資料存取層]
 * 繼承 JpaRepository 獲得基礎 CRUD 功能。
 * 方法名稱遵循 Spring Data JPA 命名規範，系統會自動生成對應 SQL。
*/
public interface ContentRepo extends JpaRepository<ContentBlock, Long> {
    
    Optional<ContentBlock> findByPageNameAndKey(String pageName, String key);
    List<ContentBlock> findByPageNameAndKeyIn(String pageName, Collection<String> keys);
    List<ContentBlock> findByPageNameOrderByKeyAsc(String pageName);
}
