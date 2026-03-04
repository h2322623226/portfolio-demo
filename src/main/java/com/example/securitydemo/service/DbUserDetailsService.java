package com.example.securitydemo.service;

import com.example.securitydemo.model.AppUser;
import com.example.securitydemo.repo.UserRepo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 數據庫使用者認證服務
 * 實作 UserDetailsService 介面，將系統定義的 AppUser 實體與 Spring Security 認證框架對接。
 */
@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public DbUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * 加載使用者身分與權限數據
     * 負責從 UserRepo 檢索數據，並將 AppRole 集合封裝為 GrantedAuthority 物件。
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 檢索使用者實體，若數據不存在則拋出符合規範的 UsernameNotFoundException
        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Security context: User not found - " + username));

        // 將業務實體的權限集合轉換為 Security 框架要求的 SimpleGrantedAuthority 格式
        var authorities = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getRole()))
                .collect(Collectors.toSet());

        // 構建 UserDetails 實例，包含憑證、權限集與帳號啟用狀態 (Enabled/Disabled)
        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .disabled(!user.isEnabled())
                .build();
    }
}
