package com.coursemanagement.rest;

import com.coursemanagement.annotation.AdminAccessLevel;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@AdminAccessLevel
@RestController
@RequestMapping(value = "/api/admin")
@RequiredArgsConstructor
public class AdminResource {
    private final UserService userService;

    @PostMapping(value = "/assign-role")
    public UserDto assignRole(@RequestBody RoleAssignmentDto roleAssignmentDto) {
        return userService.assignRole(roleAssignmentDto);
    }
}
