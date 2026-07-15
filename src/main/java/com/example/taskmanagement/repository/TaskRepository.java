package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    List<Task> findByUserIdAndStatus(Long userId, Integer status);

    List<Task> findByUserIdAndPriority(Long userId, Integer priority);

    List<Task> findByUserIdAndDueDate(Long userId, LocalDate dueDate);

    List<Task> findByUserIdAndTitleContainingIgnoreCase(
            Long userId,
            String keyword
    );
}