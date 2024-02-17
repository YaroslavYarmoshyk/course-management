package com.coursemanagement.unit.service;

import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.exception.SystemException;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.RoleRepository;
import com.coursemanagement.repository.UserRepository;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.service.impl.UserServiceImpl;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.USER_TEST_MODEL;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(value = {
        MockitoExtension.class,
        InstancioExtension.class
})
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class UserServiceImplTest {
    @InjectMocks
    @Spy
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Spy
    private ModelMapper mapper;

    @Order(1)
    @TestFactory
    @DisplayName("Test get user")
    Stream<DynamicTest> testGetUser() {
        final UserEntity userEntity = mapper.map(FIRST_STUDENT, UserEntity.class);
        final List<UserEntity> userEntities = Instancio.ofSet(USER_TEST_MODEL).create().stream()
                .map(user -> mapper.map(user, UserEntity.class))
                .toList();
        final String email = userEntity.getEmail();
        final Long id = userEntity.getId();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));
        when(userRepository.findAll()).thenReturn(userEntities);

        return Stream.of(
                dynamicTest("Test get user by email", () -> testGetUserByEmail(email)),
                dynamicTest("Test get user by id", () -> testGetUserById(id)),
                dynamicTest("Test get all users", () -> {
                    userService.getAllUsers();
                    verify(userRepository).findAll();
                }),
                dynamicTest("Test user activation", () -> testUserActivation(id)),
                dynamicTest("Test resolve current user", () -> testResolveCurrentUser(email))
        );
    }

    private void testGetUserByEmail(final String email) {
        final String nonExistingEmail = String.format("%s-%s", email, "invalid");

        final User userByEmail = userService.getUserByEmail(email);

        assertNotNull(userByEmail);
        assertThrowsWithMessage(
                () -> userService.getUserByEmail(nonExistingEmail),
                SystemException.class,
                "User by email " + nonExistingEmail + " not found"
        );
    }

    private void testGetUserById(final Long id) {
        final Long nonExistingId = id + 1;

        final User userById = userService.getUserById(id);

        assertNotNull(userById);
        assertThrowsWithMessage(
                () -> userService.getUserById(nonExistingId),
                SystemException.class,
                "User not found"
        );
    }

    private void testUserActivation(final Long id) {
        final User inactiveUser = Instancio.of(USER_TEST_MODEL)
                .set(field(User::getStatus), UserStatus.INACTIVE)
                .create();
        final ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        when(userService.getUserById(id)).thenReturn(inactiveUser);
        when(userRepository.save(any())).thenReturn(new UserEntity());

        userService.activateById(id);

        verify(userService).save(userArgumentCaptor.capture());
        final User userToSave = userArgumentCaptor.getValue();
        assertEquals(UserStatus.ACTIVE, userToSave.getStatus());
    }

    void testResolveCurrentUser(final String email) {
        final Authentication authentication = new UsernamePasswordAuthenticationToken(email, "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final User currentUser = userService.resolveCurrentUser();

        assertNotNull(currentUser);
        assertEquals(email, currentUser.getEmail());

        SecurityContextHolder.getContext().setAuthentication(null);
        assertThrowsWithMessage(
                () -> userService.resolveCurrentUser(),
                SystemException.class,
                "Cannot resolve current user, the user is unauthorized"
        );
    }
}