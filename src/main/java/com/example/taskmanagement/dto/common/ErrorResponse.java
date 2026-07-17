package com.example.taskmanagement.dto.common;

import java.time.LocalDateTime;

public class ErrorResponse {

    private LocalDateTime timestamp;

    private Integer status;

    private String code;

    private String message;

    private String path;

    public ErrorResponse() {
    }

    public ErrorResponse(
            LocalDateTime timestamp,
            Integer status,
            String code,
            String message,
            String path
    ) {
        this.timestamp = timestamp;
        this.status = status;
        this.code = code;
        this.message = message;
        this.path = path;
    }

    public static ErrorResponse of(
            Integer status,
            String code,
            String message,
            String path
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                code,
                message,
                path
        );
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
