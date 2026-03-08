package com.arauta.portfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
            // 靜態資源
            .requestMatchers("/css/**", "/js/**", "/img/**", "/video/**", "/webjars/**", "/assets/**").permitAll()
            // 其餘公開頁面與認證相關頁面
            .requestMatchers("/public/**").permitAll()
            .requestMatchers("/login", "/register").permitAll()
            // 需要登入
            .requestMatchers("/change-password").authenticated()
            // 需要 ADMIN 角色
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        );

        http.formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/public/", true)
            .failureUrl("/login?error")
            .permitAll()
        );

        http.logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/public/")
        );

        return http.build();
    }
}
