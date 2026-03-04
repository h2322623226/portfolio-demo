package com.example.securitydemo.service;

import com.example.securitydemo.repo.UserRepo;
import com.example.securitydemo.model.AppUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepo userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @Transactional
    public void register(String username, String rawPassword) {
        if (userRepo.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException();
        }
        AppUser user = new AppUser(username, encoder.encode(rawPassword));
        user.addRole("ROLE_USER");
        userRepo.save(user);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        if (!encoder.matches(oldPassword, user.getPasswordHash())) {
            throw new WrongPasswordException();
        }
        
        user.setPasswordHash(encoder.encode(newPassword));
        userRepo.save(user);
    }

    public static class UsernameAlreadyExistsException extends RuntimeException {
        public UsernameAlreadyExistsException() {
            super("Username already exists in the system.");
        }
    }

    public static class WrongPasswordException extends RuntimeException {
        public WrongPasswordException() {
            super("Current password is incorrect.");
        }
    }
}
