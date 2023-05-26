package com.coursemanagement.rest.dto;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.UserStatus;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserStatus status;
    private Set<Role> roles;
}
