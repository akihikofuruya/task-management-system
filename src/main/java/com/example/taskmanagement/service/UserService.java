package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.auth.LoginRequest;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.exception.AuthenticationFailedException;
import com.example.taskmanagement.exception.DuplicateEmailException;
import com.example.taskmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(String name, String email, String password) {
        String normalizedEmail = normalizeEmail(email);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException("メールアドレスは既に登録されています。");
        }

        User user = new User(
                normalizeName(name),
                normalizedEmail,
                password
        );

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(normalizeEmail(email));
    }

    @Transactional(readOnly = true)
    public User authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(request.getEmail()))
                .orElseThrow(this::authenticationFailed);

        if (!matchesPassword(request.getPassword(), user.getPassword())) {
            throw authenticationFailed();
        }

        return user;
    }

    private AuthenticationFailedException authenticationFailed() {
        return new AuthenticationFailedException("メールアドレスまたはパスワードが正しくありません。");
    }

    private boolean matchesPassword(String rawPassword, String savedPassword) {
        return rawPassword != null && rawPassword.equals(savedPassword);
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }

        return email.trim().toLowerCase();
    }

    private String normalizeName(String name) {
        if (name == null) {
            return null;
        }

        return name.trim();
    }
}
