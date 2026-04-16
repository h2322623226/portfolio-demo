package com.arauta.portfolio.service;

import com.arauta.portfolio.model.AppUser;
import com.arauta.portfolio.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioUserDetailsServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private PortfolioUserDetailsService service;

    // ──────────────────────────────────────────
    // 正常載入
    // ──────────────────────────────────────────

    @Test
    void loadUserByUsername_existingUser_returnsUserDetails() {
        AppUser user = new AppUser("alice", "$2a$hash");
        user.addRole("ROLE_USER");
        when(userRepo.findByUsernameWithRoles("alice")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("alice");

        assertThat(details.getUsername()).isEqualTo("alice");
        assertThat(details.getPassword()).isEqualTo("$2a$hash");
    }

    @Test
    void loadUserByUsername_userWithRoleUser_hasUserAuthority() {
        AppUser user = new AppUser("alice", "$2a$hash");
        user.addRole("ROLE_USER");
        when(userRepo.findByUsernameWithRoles("alice")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("alice");

        assertThat(details.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    @Test
    void loadUserByUsername_userWithRoleAdmin_hasAdminAuthority() {
        AppUser admin = new AppUser("boss", "$2a$hash");
        admin.addRole("ROLE_ADMIN");
        when(userRepo.findByUsernameWithRoles("boss")).thenReturn(Optional.of(admin));

        UserDetails details = service.loadUserByUsername("boss");

        assertThat(details.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @Test
    void loadUserByUsername_returnedPassword_isHashNotPlaintext() {
        AppUser user = new AppUser("alice", "$2a$hashed");
        user.addRole("ROLE_USER");
        when(userRepo.findByUsernameWithRoles("alice")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("alice");

        assertThat(details.getPassword()).isEqualTo("$2a$hashed");
        assertThat(details.getPassword()).doesNotContain("plaintext");
    }

    @Test
    void loadUserByUsername_enabledUser_isEnabled() {
        AppUser user = new AppUser("alice", "$2a$hash");
        user.addRole("ROLE_USER");
        when(userRepo.findByUsernameWithRoles("alice")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("alice");

        assertThat(details.isEnabled()).isTrue();
    }

    // ──────────────────────────────────────────
    // Spring Security 合約：找不到 user 必須拋出 UsernameNotFoundException
    // ──────────────────────────────────────────

    @Test
    void loadUserByUsername_nonExistentUser_throwsUsernameNotFoundException() {
        when(userRepo.findByUsernameWithRoles("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void loadUserByUsername_usernameInExceptionMessage() {
        when(userRepo.findByUsernameWithRoles("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("missing");
    }
}
