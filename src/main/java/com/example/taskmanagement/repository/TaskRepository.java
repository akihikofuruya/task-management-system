package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    List<Task> findByUserIdAndStatus(Long userId, Integer status);

    List<Task> findByUserIdAndPriority(Long userId, Integer priority);

    List<Task> findByUserIdAndDueDate(Long userId, LocalDate dueDate);

    List<Task> findByUserIdAndTitleContainingIgnoreCase(
            Long userId,
            String keyword
    );

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    List<Task> findAllByUserIdOrderByDueDateAsc(Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    @Query("""
            SELECT t
            FROM Task t
            WHERE t.user.id = :userId
              AND (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')))
              AND (:status IS NULL OR t.status = :status)
              AND (:priority IS NULL OR t.priority = :priority)
              AND (:fromDate IS NULL OR t.dueDate >= :fromDate)
              AND (:toDate IS NULL OR t.dueDate <= :toDate)
            ORDER BY t.dueDate ASC, t.id ASC
            """)
    List<Task> searchTasks(
            @Param("userId") Long userId,
            @Param("title") String title,
            @Param("status") Integer status,
            @Param("priority") Integer priority,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
}
