package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.task.TaskCreateRequest;
import com.example.taskmanagement.dto.task.TaskResponse;
import com.example.taskmanagement.dto.task.TaskSearchRequest;
import com.example.taskmanagement.dto.task.TaskStatusRequest;
import com.example.taskmanagement.dto.task.TaskUpdateRequest;
import com.example.taskmanagement.exception.TaskNotFoundException;
import com.example.taskmanagement.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void getTasksReturnsTaskList() throws Exception {
        when(taskService.findTasks(eq(1L), any(TaskSearchRequest.class)))
                .thenReturn(List.of(createTaskResponse()));

        mockMvc.perform(get("/api/tasks")
                        .header(USER_ID_HEADER, "1")
                        .param("title", "Java")
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java study"))
                .andExpect(jsonPath("$[0].status").value(1));
    }

    @Test
    void createTaskReturnsCreatedTask() throws Exception {
        when(taskService.createTask(any(TaskCreateRequest.class), eq(1L)))
                .thenReturn(createTaskResponse());

        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Java study");
        request.setContent("Spring Boot");
        request.setDueDate(LocalDate.of(2026, 7, 20));
        request.setPriority(2);
        request.setStatus(1);

        mockMvc.perform(post("/api/tasks")
                        .header(USER_ID_HEADER, "1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Java study"));
    }

    @Test
    void getTaskReturnsTask() throws Exception {
        when(taskService.findTaskById(10L, 1L)).thenReturn(createTaskResponse());

        mockMvc.perform(get("/api/tasks/10")
                        .header(USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void updateTaskReturnsUpdatedTask() throws Exception {
        when(taskService.updateTask(
                eq(10L),
                any(TaskUpdateRequest.class),
                eq(1L)
        )).thenReturn(createTaskResponse());

        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Java study");
        request.setContent("Spring Boot");
        request.setDueDate(LocalDate.of(2026, 7, 20));
        request.setPriority(2);
        request.setStatus(1);

        mockMvc.perform(put("/api/tasks/10")
                        .header(USER_ID_HEADER, "1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void deleteTaskReturnsNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(10L, 1L);

        mockMvc.perform(delete("/api/tasks/10")
                        .header(USER_ID_HEADER, "1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(10L, 1L);
    }

    @Test
    void updateStatusReturnsUpdatedTask() throws Exception {
        when(taskService.updateTaskStatus(
                eq(10L),
                any(TaskStatusRequest.class),
                eq(1L)
        )).thenReturn(createTaskResponse());

        mockMvc.perform(patch("/api/tasks/10/status")
                        .header(USER_ID_HEADER, "1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new TaskStatusRequest(1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1));
    }

    @Test
    void createTaskReturnsValidationErrorWhenRequestIsInvalid() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("");
        request.setDueDate(LocalDate.of(2026, 7, 20));
        request.setPriority(9);
        request.setStatus(0);

        mockMvc.perform(post("/api/tasks")
                        .header(USER_ID_HEADER, "1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getTaskReturnsNotFoundWhenTaskDoesNotExist() throws Exception {
        when(taskService.findTaskById(99L, 1L))
                .thenThrow(new TaskNotFoundException("タスクが見つかりません。"));

        mockMvc.perform(get("/api/tasks/99")
                        .header(USER_ID_HEADER, "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TASK_001"));
    }

    @Test
    void getTasksReturnsValidationErrorWhenDateRangeIsInvalid() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .header(USER_ID_HEADER, "1")
                        .param("fromDate", "2026-07-31")
                        .param("toDate", "2026-07-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    private TaskResponse createTaskResponse() {
        return new TaskResponse(
                10L,
                "Java study",
                "Spring Boot",
                LocalDate.of(2026, 7, 20),
                2,
                "中",
                1,
                "対応中",
                LocalDateTime.of(2026, 7, 17, 10, 0),
                LocalDateTime.of(2026, 7, 17, 11, 0)
        );
    }
}
