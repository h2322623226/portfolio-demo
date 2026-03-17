package com.arauta.portfolio.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<AppRole> roles = new HashSet<>();

    protected AppUser() { }

    public AppUser(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public void setPasswordHash(String passwordHash) { 
        this.passwordHash = passwordHash; 
    }

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
