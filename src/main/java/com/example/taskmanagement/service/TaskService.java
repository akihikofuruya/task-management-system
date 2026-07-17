package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.task.TaskCreateRequest;
import com.example.taskmanagement.dto.task.TaskResponse;
import com.example.taskmanagement.dto.task.TaskSearchRequest;
import com.example.taskmanagement.dto.task.TaskStatusRequest;
import com.example.taskmanagement.dto.task.TaskUpdateRequest;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.exception.TaskNotFoundException;
import com.example.taskmanagement.exception.UserNotFoundException;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            UserRepository userRepository
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findTasks(Long userId, TaskSearchRequest request) {
        TaskSearchRequest searchRequest = request == null
                ? new TaskSearchRequest()
                : request;

        return taskRepository.searchTasks(
                        userId,
                        normalizeKeyword(searchRequest.getTitle()),
                        searchRequest.getStatus(),
                        searchRequest.getPriority(),
                        searchRequest.getFromDate(),
                        searchRequest.getToDate()
                )
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findTaskById(Long taskId, Long userId) {
        return TaskResponse.from(findOwnedTask(taskId, userId));
    }

    @Transactional
    public TaskResponse createTask(TaskCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ユーザーが見つかりません。"));

        Task task = new Task(
                user,
                request.getTitle(),
                request.getContent(),
                request.getDueDate(),
                request.getPriority(),
                request.getStatus()
        );

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTask(
            Long taskId,
            TaskUpdateRequest request,
            Long userId
    ) {
        Task task = findOwnedTask(taskId, userId);

        task.setTitle(request.getTitle());
        task.setContent(request.getContent());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        Task task = findOwnedTask(taskId, userId);

        taskRepository.delete(task);
    }

    @Transactional
    public TaskResponse updateTaskStatus(
            Long taskId,
            TaskStatusRequest request,
            Long userId
    ) {
        Task task = findOwnedTask(taskId, userId);

        task.setStatus(request.getStatus());

        return TaskResponse.from(taskRepository.save(task));
    }

    private Task findOwnedTask(Long taskId, Long userId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException("タスクが見つかりません。"));
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }
}
