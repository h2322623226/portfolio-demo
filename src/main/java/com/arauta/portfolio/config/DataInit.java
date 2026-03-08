package com.arauta.portfolio.config;

import com.arauta.portfolio.model.AppUser;
import com.arauta.portfolio.repo.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInit(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // createIfAbsent("初始帳號",  "初始密碼",  "ROLE_ADMIN", "ROLE_USER");
    }

    private void createIfAbsent(String username, String rawPassword, String... roles) {
        if (userRepo.findByUsername(username).isPresent()) return;

        AppUser user = new AppUser(username, passwordEncoder.encode(rawPassword));
        for (String role : roles) {
            user.addRole(role);
        }
        userRepo.save(user);
        System.out.println("[DataInit] 建立帳號：" + username);
    }
}
