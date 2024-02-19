package com.coursemanagement.unit.service;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.service.UserService;
import com.coursemanagement.service.impl.RoleManagementServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.NEW_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
class RoleManagementServiceImplTest {
    @InjectMocks
    private RoleManagementServiceImpl roleManagementService;
    @Mock
    private UserService userService;

    @Test
    @DisplayName("Test add role to user")
    void testAddRoleToUser() {
        final ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        when(userService.getUserById(NEW_USER.getId())).thenReturn(NEW_USER);
        when(userService.save(any(User.class))).thenReturn(FIRST_STUDENT);

        roleManagementService.assignRoleToUser(new RoleAssignmentDto(NEW_USER.getId(), Set.of(Role.STUDENT)));

        verify(userService).save(userArgumentCaptor.capture());
        assertTrue(userArgumentCaptor.getValue().getRoles().contains(Role.STUDENT));
    }
}