package com.coursemanagement.rest;

import com.coursemanagement.annotation.AdminAccessLevel;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.service.RoleManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/users")
@RequiredArgsConstructor
public class UserResource {
    private final RoleManagementService roleManagementService;

    @AdminAccessLevel
    @PostMapping(value = "/assign-role")
    public User assignRoleToUser(@RequestBody final RoleAssignmentDto roleAssignmentDto) {
        return roleManagementService.assignRoleToUser(roleAssignmentDto);
    }
}
