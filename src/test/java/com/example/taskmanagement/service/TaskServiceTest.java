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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void findTasksSearchesByUserWithoutConditions() {
        User user = new User("Test User", "user@example.com", "password123");
        Task task = new Task(
                user,
                "Task title",
                "Task content",
                LocalDate.of(2026, 7, 20),
                2,
                0
        );

        when(taskRepository.searchTasks(
                1L,
                null,
                null,
                null,
                null,
                null
        )).thenReturn(List.of(task));

        List<TaskResponse> result = taskService.findTasks(1L, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Task title");
    }

    @Test
    void findTasksSearchesByUserAndConditions() {
        TaskSearchRequest request = new TaskSearchRequest();
        request.setTitle(" Java ");
        request.setStatus(1);
        request.setPriority(2);
        request.setFromDate(LocalDate.of(2026, 7, 1));
        request.setToDate(LocalDate.of(2026, 7, 31));

        User user = new User("Test User", "user@example.com", "password123");
        Task task = new Task(
                user,
                "Java study",
                "Spring Boot",
                LocalDate.of(2026, 7, 20),
                2,
                1
        );

        when(taskRepository.searchTasks(
                1L,
                "Java",
                1,
                2,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31)
        )).thenReturn(List.of(task));

        List<TaskResponse> result = taskService.findTasks(1L, request);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Java study");
    }

    @Test
    void findTasksReturnsEmptyListWhenNoTasksMatch() {
        TaskSearchRequest request = new TaskSearchRequest();
        request.setTitle("Not found");

        when(taskRepository.searchTasks(
                1L,
                "Not found",
                null,
                null,
                null,
                null
        )).thenReturn(List.of());

        List<TaskResponse> result = taskService.findTasks(1L, request);

        assertThat(result).isEmpty();
    }

    @Test
    void createTaskSavesTaskForUser() {
        User user = new User("Test User", "user@example.com", "password123");
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Task title");
        request.setContent("Task content");
        request.setDueDate(LocalDate.of(2026, 7, 20));
        request.setPriority(2);
        request.setStatus(0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse result = taskService.createTask(request, 1L);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        Task savedTask = taskCaptor.getValue();
        assertThat(savedTask.getUser()).isSameAs(user);
        assertThat(savedTask.getTitle()).isEqualTo("Task title");
        assertThat(savedTask.getContent()).isEqualTo("Task content");
        assertThat(savedTask.getDueDate()).isEqualTo(LocalDate.of(2026, 7, 20));
        assertThat(result.getTitle()).isEqualTo("Task title");
    }

    @Test
    void createTaskThrowsExceptionWhenUserDoesNotExist() {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Task title");
        request.setContent("Task content");
        request.setDueDate(LocalDate.of(2026, 7, 20));
        request.setPriority(2);
        request.setStatus(0);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.createTask(request, 99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("ユーザーが見つかりません。");
    }

    @Test
    void findTaskByIdThrowsExceptionWhenTaskDoesNotExistForUser() {
        when(taskRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findTaskById(99L, 1L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("タスクが見つかりません。");
    }

    @Test
    void updateTaskUpdatesOwnedTask() {
        User user = new User("Test User", "user@example.com", "password123");
        Task task = new Task(
                user,
                "Old title",
                "Old content",
                LocalDate.of(2026, 7, 20),
                2,
                0
        );
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("New title");
        request.setContent("New content");
        request.setDueDate(LocalDate.of(2026, 7, 21));
        request.setPriority(1);
        request.setStatus(1);

        when(taskRepository.findByIdAndUserId(1L, 10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        TaskResponse result = taskService.updateTask(1L, request, 10L);

        assertThat(task.getTitle()).isEqualTo("New title");
        assertThat(task.getContent()).isEqualTo("New content");
        assertThat(task.getDueDate()).isEqualTo(LocalDate.of(2026, 7, 21));
        assertThat(task.getPriority()).isEqualTo(1);
        assertThat(task.getStatus()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("New title");
    }

    @Test
    void updateTaskStatusUpdatesOnlyStatus() {
        User user = new User("Test User", "user@example.com", "password123");
        Task task = new Task(
                user,
                "Task title",
                "Task content",
                LocalDate.of(2026, 7, 20),
                2,
                0
        );

        when(taskRepository.findByIdAndUserId(1L, 10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        TaskResponse result = taskService.updateTaskStatus(
                1L,
                new TaskStatusRequest(2),
                10L
        );

        assertThat(task.getStatus()).isEqualTo(2);
        assertThat(result.getStatus()).isEqualTo(2);
    }

    @Test
    void deleteTaskDeletesOwnedTask() {
        User user = new User("Test User", "user@example.com", "password123");
        Task task = new Task(
                user,
                "Task title",
                "Task content",
                LocalDate.of(2026, 7, 20),
                2,
                0
        );

        when(taskRepository.findByIdAndUserId(1L, 10L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L, 10L);

        verify(taskRepository).delete(task);
    }
}
