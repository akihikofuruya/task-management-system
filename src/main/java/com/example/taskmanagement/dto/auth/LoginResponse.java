package com.example.taskmanagement.dto.auth;

import com.example.taskmanagement.entity.User;

public class LoginResponse {

    private String accessToken;

    private String tokenType;

    private Long expiresIn;

    private Long userId;

    private String userName;

    private String email;

    public LoginResponse() {
    }

    public LoginResponse(
            String accessToken,
            String tokenType,
            Long expiresIn,
            Long userId,
            String userName,
            String email
    ) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
    }

    public static LoginResponse from(
            User user,
            String accessToken,
            Long expiresIn
    ) {
        return new LoginResponse(
                accessToken,
                "Bearer",
                expiresIn,
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
