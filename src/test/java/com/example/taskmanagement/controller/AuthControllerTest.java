package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.auth.LoginRequest;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.exception.AuthenticationFailedException;
import com.example.taskmanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void loginReturnsLoginResponse() throws Exception {
        User user = new User("Test User", "user@example.com", "password123");
        when(userService.authenticate(any(LoginRequest.class))).thenReturn(user);

        LoginRequest request = new LoginRequest(
                "user@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.userName").value("Test User"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void logoutReturnsNoContent() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    void loginReturnsUnauthorizedWhenAuthenticationFails() throws Exception {
        when(userService.authenticate(any(LoginRequest.class)))
                .thenThrow(new AuthenticationFailedException("メールアドレスまたはパスワードが正しくありません。"));

        LoginRequest request = new LoginRequest(
                "user@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_001"));
    }

    @Test
    void loginReturnsValidationErrorWhenRequestIsInvalid() throws Exception {
        LoginRequest request = new LoginRequest(
                "invalid-email",
                "short"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
