package com.coursemanagement.repository;

import com.coursemanagement.config.CourseManagementTestContainer;
import com.coursemanagement.repository.entity.UserEntity;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @ClassRule
    public static PostgreSQLContainer<CourseManagementTestContainer> postgreSQLContainer = CourseManagementTestContainer.getInstance();

    @Test
    public void shouldReturnUserWithRoles() {
        final Optional<UserEntity> byEmail = userRepository.findByEmail("john-smith@gmail.com");
    }
}