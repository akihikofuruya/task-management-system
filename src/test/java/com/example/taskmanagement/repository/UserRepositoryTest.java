package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmailReturnsUserWhenEmailExists() {
        User user = userRepository.save(new User(
                "Test User",
                "user@example.com",
                "password123"
        ));

        Optional<User> result = userRepository.findByEmail("user@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        assertThat(result.get().getName()).isEqualTo("Test User");
    }

    @Test
    void findByEmailReturnsEmptyWhenEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("missing@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmailReturnsTrueWhenEmailExists() {
        userRepository.save(new User(
                "Test User",
                "user@example.com",
                "password123"
        ));

        boolean result = userRepository.existsByEmail("user@example.com");

        assertThat(result).isTrue();
    }

    @Test
    void existsByEmailReturnsFalseWhenEmailDoesNotExist() {
        boolean result = userRepository.existsByEmail("missing@example.com");

        assertThat(result).isFalse();
    }
}
