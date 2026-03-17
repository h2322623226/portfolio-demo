package com.arauta.portfolio.model;

import jakarta.persistence.*;

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
