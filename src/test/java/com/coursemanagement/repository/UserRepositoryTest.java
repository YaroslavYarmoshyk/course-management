package com.coursemanagement.repository;

import com.coursemanagement.config.DatabaseSetupExtension;
import com.coursemanagement.config.properties.UserTestDataProperties;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(showSql = false)
@ExtendWith(DatabaseSetupExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableConfigurationProperties(UserTestDataProperties.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTestDataProperties userTestDataProperties;
    private User admin;

    @BeforeEach
    void setUp() {
        admin = userTestDataProperties.getAdmin();
    }

    @Test
    @Order(1)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find user by email with fetched roles")
    void testFindUserByEmail_Roles_Are_Fetched() {
        final String adminEmail = admin.getEmail();
        final Optional<UserEntity> userByEmail = userRepository.findByEmail(adminEmail);
        assertTrue(userByEmail.isPresent());

        final Set<Role> actualRoles = userByEmail.get().getRoles()
                .stream()
                .map(RoleEntity::getRole)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
        final Set<Role> expectedRoles = Stream.of(Role.ADMIN, Role.STUDENT)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
        assertIterableEquals(expectedRoles, actualRoles);
    }

    @Test
    @Order(2)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test find user by id with fetched roles")
    void testFindUserById_Roles_Are_Fetched() {
        final Long id = admin.getId();
        final Optional<UserEntity> userById = userRepository.findById(id);
        assertTrue(userById.isPresent());

        final Set<Role> actualRoles = userById.get().getRoles()
                .stream()
                .map(RoleEntity::getRole)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
        final Set<Role> expectedRoles = Stream.of(Role.ADMIN, Role.STUDENT)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
        assertIterableEquals(expectedRoles, actualRoles);
    }
}