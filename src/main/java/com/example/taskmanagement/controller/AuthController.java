package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.auth.LoginRequest;
import com.example.taskmanagement.dto.auth.LoginResponse;
import com.example.taskmanagement.dto.auth.RegisterRequest;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Long TEMPORARY_TOKEN_EXPIRES_IN = 3600L;

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = userService.registerUser(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(LoginResponse.from(
                        user,
                        createTemporaryToken(user),
                        TEMPORARY_TOKEN_EXPIRES_IN
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        User user = userService.authenticate(request);

        return ResponseEntity.ok(LoginResponse.from(
                user,
                createTemporaryToken(user),
                TEMPORARY_TOKEN_EXPIRES_IN
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    private String createTemporaryToken(User user) {
        return "temporary-token-" + user.getId();
    }
}
