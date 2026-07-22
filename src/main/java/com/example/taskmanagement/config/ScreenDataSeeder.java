package com.example.taskmanagement.config;

import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("screen")
public class ScreenDataSeeder {

    public static final String DEMO_EMAIL = "demo@example.com";

    public static final String DEMO_PASSWORD = "password";

    @Bean
    CommandLineRunner seedScreenData(
            UserRepository userRepository,
            TaskRepository taskRepository
    ) {
        return args -> {
            if (userRepository.existsByEmail(DEMO_EMAIL)) {
                return;
            }

            User demoUser = userRepository.save(new User(
                    "Demo User",
                    DEMO_EMAIL,
                    DEMO_PASSWORD
            ));

            LocalDate today = LocalDate.now();

            taskRepository.saveAll(List.of(
                    new Task(
                            demoUser,
                            "Review task requirements",
                            "Check the current specification and update the task scope.",
                            today,
                            1,
                            1
                    ),
                    new Task(
                            demoUser,
                            "Create controller tests",
                            "Add request validation and error response coverage.",
                            today.plusDays(1),
                            1,
                            0
                    ),
                    new Task(
                            demoUser,
                            "Update screen templates",
                            "Adjust form layout and confirm browser behavior.",
                            today.plusDays(3),
                            2,
                            0
                    ),
                    new Task(
                            demoUser,
                            "Prepare demo notes",
                            "Summarize login account, task flow, and known limitations.",
                            today.plusDays(5),
                            2,
                            1
                    ),
                    new Task(
                            demoUser,
                            "Repository test pass",
                            "Repository and service tests were completed successfully.",
                            today.minusDays(1),
                            3,
                            2
                    )
            ));
        };
    }
}
