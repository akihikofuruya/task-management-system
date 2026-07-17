package com.example.taskmanagement.dto.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse {

    private LocalDateTime timestamp;

    private Integer status;

    private String code;

    private String message;

    private List<FieldErrorResponse> details = new ArrayList<>();

    public ValidationErrorResponse() {
    }

    public ValidationErrorResponse(
            LocalDateTime timestamp,
            Integer status,
            String code,
            String message,
            List<FieldErrorResponse> details
    ) {
        this.timestamp = timestamp;
        this.status = status;
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public static ValidationErrorResponse of(
            Integer status,
            String message,
            List<FieldErrorResponse> details
    ) {
        return new ValidationErrorResponse(
                LocalDateTime.now(),
                status,
                "VALIDATION_ERROR",
                message,
                details
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

    public List<FieldErrorResponse> getDetails() {
        return details;
    }

    public void setDetails(List<FieldErrorResponse> details) {
        this.details = details;
    }
}
