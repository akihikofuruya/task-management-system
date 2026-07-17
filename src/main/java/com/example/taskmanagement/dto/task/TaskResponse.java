package com.example.taskmanagement.dto.task;

import com.example.taskmanagement.entity.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskResponse {

    private Long id;

    private String title;

    private String content;

    private LocalDate dueDate;

    private Integer priority;

    private String priorityName;

    private Integer status;

    private String statusName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public TaskResponse() {
    }

    public TaskResponse(
            Long id,
            String title,
            String content,
            LocalDate dueDate,
            Integer priority,
            String priorityName,
            Integer status,
            String statusName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
        this.priority = priority;
        this.priorityName = priorityName;
        this.status = status;
        this.statusName = statusName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getContent(),
                task.getDueDate(),
                task.getPriority(),
                resolvePriorityName(task.getPriority()),
                task.getStatus(),
                resolveStatusName(task.getStatus()),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    private static String resolvePriorityName(Integer priority) {
        if (priority == null) {
            return null;
        }

        return switch (priority) {
            case 1 -> "高";
            case 2 -> "中";
            case 3 -> "低";
            default -> null;
        };
    }

    private static String resolveStatusName(Integer status) {
        if (status == null) {
            return null;
        }

        return switch (status) {
            case 0 -> "未着手";
            case 1 -> "対応中";
            case 2 -> "完了";
            default -> null;
        };
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
