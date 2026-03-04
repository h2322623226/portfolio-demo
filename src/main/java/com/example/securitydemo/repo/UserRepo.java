package com.example.securitydemo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.securitydemo.model.AppUser;
import java.util.Optional;

/**
 * [使用者帳號存取層]
 * 繼承 JpaRepository 獲得基礎 CRUD 功能。
 * 負責所有與帳號相關的資料庫互動。
 */
public interface UserRepo extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
