package com.arauta.portfolio.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.arauta.portfolio.model.AppUser;
import java.util.Optional;

/**
 * [使用者帳號存取層]
 * 繼承 JpaRepository 獲得基礎 CRUD 功能。
 * 負責所有與帳號相關的資料庫互動。
 */
@Repository
public interface UserRepo extends JpaRepository<AppUser, Long> {

    /**
     * 一般查詢（不載入 roles），用於 UserService 的帳號操作。
     */
    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);

    /**
     * 認證專用查詢：JOIN FETCH roles，避免 LAZY 載入在 Security context 外失效。
     * 僅在 PortfolioUserDetailsService 使用。
     */
    @Query("SELECT u FROM AppUser u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<AppUser> findByUsernameWithRoles(@Param("username") String username);
}
