package com.coursemanagement.repository;

import com.coursemanagement.config.DatabaseSetupExtension;
import com.coursemanagement.config.UserProperties;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserEntity;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(showSql = false)
@ExtendWith(DatabaseSetupExtension.class)
@EnableConfigurationProperties(UserProperties.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProperties userProperties;
    public static User ADMIN;

    @PostConstruct
    public void init() {
        ADMIN = userProperties.getAdmin();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test getting user by email with fetched roles")
    void shouldReturnUserWithFetchedRolesByEmail() {
        final String adminEmail = ADMIN.getEmail();
        final Optional<UserEntity> userByEmail = userRepository.findByEmail(adminEmail);
        assertTrue(userByEmail.isPresent());

        final UserEntity userEntity = userByEmail.get();
        assertEquals(adminEmail, userEntity.getEmail());

        final Set<Role> actualRoles = userEntity.getRoles()
                .stream()
                .map(RoleEntity::getRole)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
        final Set<Role> expectedRoles = Set.of(Role.ADMIN, Role.STUDENT).stream()
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
        assertIterableEquals(expectedRoles, actualRoles);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName(value = "Test getting user by id with fetched roles")
    void shouldReturnUserWithFetchedRolesById() {
        final Long id = ADMIN.getId();
        final Optional<UserEntity> userById = userRepository.findById(id);
        assertTrue(userById.isPresent());

        final UserEntity userEntity = userById.get();
        assertEquals(id, userEntity.getId());

        final Set<Role> actualRoles = userEntity.getRoles()
                .stream()
                .map(RoleEntity::getRole)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
        final Set<Role> expectedRoles = Set.of(Role.ADMIN, Role.STUDENT).stream()
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
        assertIterableEquals(expectedRoles, actualRoles);
    }
}