package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.auth.LoginRequest;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.exception.AuthenticationFailedException;
import com.example.taskmanagement.exception.DuplicateEmailException;
import com.example.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUserSavesUserWhenEmailIsUnique() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser(
                " Test User ",
                " USER@example.com ",
                "password123"
        );

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("user@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("password123");
        assertThat(result).isSameAs(savedUser);
    }

    @Test
    void registerUserThrowsExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(
                "Test User",
                "user@example.com",
                "password123"
        )).isInstanceOf(DuplicateEmailException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByEmailNormalizesEmail() {
        User user = new User("Test User", "user@example.com", "password123");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail(" USER@example.com ");

        assertThat(result).containsSame(user);
    }

    @Test
    void authenticateReturnsUserWhenPasswordMatches() {
        User user = new User("Test User", "user@example.com", "password123");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = userService.authenticate(
                new LoginRequest(" USER@example.com ", "password123")
        );

        assertThat(result).isSameAs(user);
    }

    @Test
    void authenticateThrowsExceptionWhenUserDoesNotExist() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.authenticate(
                new LoginRequest("unknown@example.com", "password123")
        )).isInstanceOf(AuthenticationFailedException.class);

        verify(userRepository).findByEmail("unknown@example.com");
    }

    @Test
    void authenticateThrowsExceptionWhenPasswordDoesNotMatch() {
        User user = new User("Test User", "user@example.com", "password123");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.authenticate(
                new LoginRequest("user@example.com", "wrongpass")
        )).isInstanceOf(AuthenticationFailedException.class);
    }
}
