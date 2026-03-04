package com.example.securitydemo.model;

import jakarta.persistence.*;

/**
 * [權限角色實體]
 * 關聯：多對一關聯到 AppUser。
 * 慣例：role 欄位的值存為 "ROLE_USER"、"ROLE_ADMIN"。
 */
@Entity
@Table(name = "app_role")
public class AppRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(nullable = false)
    private String role;

    protected AppRole() { }

    public AppRole(AppUser user, String role) {
        this.user = user;
        this.role = role;
    }

    public String getRole() { 
        return role; 
    }

    public Long getId() { 
        return id; 
    }
}
