package com.coursemanagement.service.impl;

import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.service.RoleManagementService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleManagementServiceImpl implements RoleManagementService {
    private final UserService userService;

    @Override
    public User assignRoleToUser(final RoleAssignmentDto roleAssignmentDto) {
        final User user = userService.getUserById(roleAssignmentDto.userId());
        user.getRoles().addAll(roleAssignmentDto.roles());
        return userService.save(user);
    }
}
