package com.example.taskmanagement.dto.task;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TaskStatusRequest {

    @NotNull
    @Min(0)
    @Max(2)
    private Integer status;

    public TaskStatusRequest() {
    }

    public TaskStatusRequest(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
