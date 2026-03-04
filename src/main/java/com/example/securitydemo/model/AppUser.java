package com.example.securitydemo.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * [使用者實體]
 * 關聯：與 AppRole 為一對多關係，使用 EAGER 確保驗證時即時獲取權限。
 * 狀態：enabled 欄位對接 Spring Security 的帳號啟用檢查。
 */
@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<AppRole> roles = new HashSet<>();

    protected AppUser() { }

    public AppUser(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public void setPasswordHash(String passwordHash) { 
        this.passwordHash = passwordHash; 
    }

    /**
     * 封裝新增角色的邏輯，自動建立與當前使用者的關聯。
     */
    public void addRole(String role) {
        this.roles.add(new AppRole(this, role));
    }

    public Long getId() { 
        return id; 
    }

    public String getUsername() { 
        return username; 
    }

    public String getPasswordHash() { 
        return passwordHash; 
    }

    public boolean isEnabled() { 
        return enabled; 
    }

    public Set<AppRole> getRoles() { 
        return roles; 
    }
}
