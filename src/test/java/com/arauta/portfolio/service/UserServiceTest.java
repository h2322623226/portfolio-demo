package com.arauta.portfolio.service;

import com.arauta.portfolio.model.AppUser;
import com.arauta.portfolio.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    // ──────────────────────────────────────────
    // register()
    // ──────────────────────────────────────────

    @Test
    void register_newUsername_savesUserWithEncodedPassword() {
        when(userRepo.existsByUsername("alice")).thenReturn(false);
        when(encoder.encode("rawPass")).thenReturn("$2a$hashed");

        userService.register("alice", "rawPass");

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepo).save(captor.capture());
        AppUser saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("alice");
        assertThat(saved.getPasswordHash()).isEqualTo("$2a$hashed");
    }

    @Test
    void register_existingUsername_throwsUsernameAlreadyExistsException() {
        when(userRepo.existsByUsername("alice")).thenReturn(true);

        assertThatThrownBy(() -> userService.register("alice", "rawPass"))
                .isInstanceOf(UserService.UsernameAlreadyExistsException.class);

        verify(userRepo, never()).save(any());
    }

    @Test
    void register_newUser_defaultRoleIsUser_notAdmin() {
        when(userRepo.existsByUsername("bob")).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("$2a$hashed");

        userService.register("bob", "rawPass");

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepo).save(captor.capture());
        AppUser saved = captor.getValue();
        assertThat(saved.getRoles())
                .hasSize(1)
                .allMatch(r -> r.getRole().equals("ROLE_USER"));
    }

    @Test
    void register_rawPasswordNeverStoredDirectly() {
        when(userRepo.existsByUsername("carol")).thenReturn(false);
        when(encoder.encode("secret")).thenReturn("$2a$hashed");

        userService.register("carol", "secret");

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepo).save(captor.capture());
        // 確認儲存的 hash 不等於原始密碼
        assertThat(captor.getValue().getPasswordHash()).isNotEqualTo("secret");
        verify(encoder).encode("secret");
    }

    // ──────────────────────────────────────────
    // changePassword()
    // ──────────────────────────────────────────

    @Test
    void changePassword_correctOldPassword_updatesSuccessfully() {
        AppUser user = new AppUser("dave", "$2a$oldHash");
        when(userRepo.findByUsername("dave")).thenReturn(Optional.of(user));
        when(encoder.matches("oldRaw", "$2a$oldHash")).thenReturn(true);
        when(encoder.encode("newRaw")).thenReturn("$2a$newHash");

        userService.changePassword("dave", "oldRaw", "newRaw");

        verify(userRepo).save(user);
        assertThat(user.getPasswordHash()).isEqualTo("$2a$newHash");
    }

    @Test
    void changePassword_wrongOldPassword_throwsWrongPasswordException() {
        AppUser user = new AppUser("dave", "$2a$oldHash");
        when(userRepo.findByUsername("dave")).thenReturn(Optional.of(user));
        when(encoder.matches("wrongRaw", "$2a$oldHash")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword("dave", "wrongRaw", "newRaw"))
                .isInstanceOf(UserService.WrongPasswordException.class);

        verify(userRepo, never()).save(any());
    }

    @Test
    void changePassword_newPasswordIsEncoded_notStoredAsPlaintext() {
        AppUser user = new AppUser("eve", "$2a$oldHash");
        when(userRepo.findByUsername("eve")).thenReturn(Optional.of(user));
        when(encoder.matches("oldRaw", "$2a$oldHash")).thenReturn(true);
        when(encoder.encode("newRaw")).thenReturn("$2a$newHash");

        userService.changePassword("eve", "oldRaw", "newRaw");

        assertThat(user.getPasswordHash()).isEqualTo("$2a$newHash");
        assertThat(user.getPasswordHash()).isNotEqualTo("newRaw");
    }

    @Test
    void changePassword_userNotFound_throwsIllegalArgument() {
        when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword("ghost", "any", "any"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ghost");
    }
}
