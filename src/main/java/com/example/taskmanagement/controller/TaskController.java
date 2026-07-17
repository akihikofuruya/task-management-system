package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.task.TaskCreateRequest;
import com.example.taskmanagement.dto.task.TaskResponse;
import com.example.taskmanagement.dto.task.TaskSearchRequest;
import com.example.taskmanagement.dto.task.TaskStatusRequest;
import com.example.taskmanagement.dto.task.TaskUpdateRequest;
import com.example.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @ModelAttribute TaskSearchRequest request
    ) {
        return ResponseEntity.ok(taskService.findTasks(userId, request));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody TaskCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(request, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable("id") Long taskId
    ) {
        return ResponseEntity.ok(taskService.findTaskById(taskId, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable("id") Long taskId,
            @Valid @RequestBody TaskUpdateRequest request
    ) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable("id") Long taskId
    ) {
        taskService.deleteTask(taskId, userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable("id") Long taskId,
            @Valid @RequestBody TaskStatusRequest request
    ) {
        return ResponseEntity.ok(taskService.updateTaskStatus(
                taskId,
                request,
                userId
        ));
    }
}
