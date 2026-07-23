package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class TaskRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void saveCreatesTask() {
        User user = saveUser("user@example.com");
        Task task = new Task(
                user,
                "New task",
                "new content",
                LocalDate.of(2026, 7, 20),
                2,
                0
        );

        Task savedTask = taskRepository.save(task);

        assertThat(savedTask.getId()).isNotNull();
        assertThat(taskRepository.findById(savedTask.getId()))
                .isPresent()
                .get()
                .satisfies(foundTask -> {
                    assertThat(foundTask.getTitle()).isEqualTo("New task");
                    assertThat(foundTask.getContent()).isEqualTo("new content");
                    assertThat(foundTask.getDueDate()).isEqualTo(LocalDate.of(2026, 7, 20));
                    assertThat(foundTask.getPriority()).isEqualTo(2);
                    assertThat(foundTask.getStatus()).isEqualTo(0);
                    assertThat(foundTask.getUser().getId()).isEqualTo(user.getId());
                });
    }

    @Test
    void saveUpdatesTask() {
        User user = saveUser("user@example.com");
        Task task = saveTask(user, "Before update", LocalDate.of(2026, 7, 20), 2, 0);

        task.setTitle("After update");
        task.setContent("updated content");
        task.setDueDate(LocalDate.of(2026, 7, 25));
        task.setPriority(1);
        task.setStatus(1);
        taskRepository.save(task);

        assertThat(taskRepository.findById(task.getId()))
                .isPresent()
                .get()
                .satisfies(updatedTask -> {
                    assertThat(updatedTask.getTitle()).isEqualTo("After update");
                    assertThat(updatedTask.getContent()).isEqualTo("updated content");
                    assertThat(updatedTask.getDueDate()).isEqualTo(LocalDate.of(2026, 7, 25));
                    assertThat(updatedTask.getPriority()).isEqualTo(1);
                    assertThat(updatedTask.getStatus()).isEqualTo(1);
                });
    }

    @Test
    void deleteRemovesTask() {
        User user = saveUser("user@example.com");
        Task task = saveTask(user, "Delete task", LocalDate.of(2026, 7, 20), 2, 0);

        taskRepository.delete(task);

        assertThat(taskRepository.findById(task.getId())).isEmpty();
    }

    @Test
    void findByUserIdReturnsOnlyUsersTasks() {
        User user = saveUser("user@example.com");
        User otherUser = saveUser("other@example.com");
        Task usersTask = saveTask(user, "User task", LocalDate.of(2026, 7, 20), 2, 0);
        saveTask(otherUser, "Other task", LocalDate.of(2026, 7, 21), 1, 1);

        List<Task> result = taskRepository.findByUserId(user.getId());

        assertThat(result).extracting(Task::getId).containsExactly(usersTask.getId());
    }

    @Test
    void findByIdAndUserIdReturnsTaskOnlyWhenOwnedByUser() {
        User user = saveUser("user@example.com");
        User otherUser = saveUser("other@example.com");
        Task task = saveTask(user, "User task", LocalDate.of(2026, 7, 20), 2, 0);

        Optional<Task> ownedResult = taskRepository.findByIdAndUserId(task.getId(), user.getId());
        Optional<Task> otherUserResult = taskRepository.findByIdAndUserId(task.getId(), otherUser.getId());

        assertThat(ownedResult).isPresent();
        assertThat(otherUserResult).isEmpty();
    }

    @Test
    void findAllByUserIdOrderByDueDateAscReturnsTasksInDueDateOrder() {
        User user = saveUser("user@example.com");
        Task laterTask = saveTask(user, "Later", LocalDate.of(2026, 7, 30), 2, 0);
        Task earlierTask = saveTask(user, "Earlier", LocalDate.of(2026, 7, 10), 2, 0);

        List<Task> result = taskRepository.findAllByUserIdOrderByDueDateAsc(user.getId());

        assertThat(result).extracting(Task::getId)
                .containsExactly(earlierTask.getId(), laterTask.getId());
    }

    @Test
    void existsByIdAndUserIdReturnsWhetherTaskIsOwnedByUser() {
        User user = saveUser("user@example.com");
        User otherUser = saveUser("other@example.com");
        Task task = saveTask(user, "User task", LocalDate.of(2026, 7, 20), 2, 0);

        assertThat(taskRepository.existsByIdAndUserId(task.getId(), user.getId())).isTrue();
        assertThat(taskRepository.existsByIdAndUserId(task.getId(), otherUser.getId())).isFalse();
    }

    @Test
    void searchTasksFiltersByUserAndSearchConditions() {
        User user = saveUser("user@example.com");
        User otherUser = saveUser("other@example.com");
        Task matchingTask = saveTask(
                user,
                "Java study",
                LocalDate.of(2026, 7, 20),
                1,
                1
        );
        saveTask(user, "Java done", LocalDate.of(2026, 7, 25), 1, 2);
        saveTask(user, "Spring task", LocalDate.of(2026, 7, 20), 1, 1);
        saveTask(otherUser, "Java study", LocalDate.of(2026, 7, 20), 1, 1);

        List<Task> result = taskRepository.searchTasks(
                user.getId(),
                "java",
                1,
                1,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31)
        );

        assertThat(result).extracting(Task::getId).containsExactly(matchingTask.getId());
    }

    @Test
    void searchTasksReturnsEmptyWhenNoTaskMatchesConditions() {
        User user = saveUser("user@example.com");
        saveTask(user, "Spring task", LocalDate.of(2026, 7, 20), 2, 0);

        List<Task> result = taskRepository.searchTasks(
                user.getId(),
                "java",
                1,
                1,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31)
        );

        assertThat(result).isEmpty();
    }

    @Test
    void searchTasksReturnsAllUsersTasksWhenConditionsAreNull() {
        User user = saveUser("user@example.com");
        User otherUser = saveUser("other@example.com");
        Task firstTask = saveTask(user, "First", LocalDate.of(2026, 7, 10), 2, 0);
        Task secondTask = saveTask(user, "Second", LocalDate.of(2026, 7, 20), 1, 1);
        saveTask(otherUser, "Other", LocalDate.of(2026, 7, 15), 3, 2);

        List<Task> result = taskRepository.searchTasks(
                user.getId(),
                null,
                null,
                null,
                null,
                null
        );

        assertThat(result).extracting(Task::getId)
                .containsExactly(firstTask.getId(), secondTask.getId());
    }

    private User saveUser(String email) {
        return userRepository.save(new User(
                "Test User",
                email,
                "password123"
        ));
    }

    private Task saveTask(
            User user,
            String title,
            LocalDate dueDate,
            Integer priority,
            Integer status
    ) {
        return taskRepository.save(new Task(
                user,
                title,
                "content",
                dueDate,
                priority,
                status
        ));
    }
}
