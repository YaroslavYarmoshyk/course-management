package com.coursemanagement.model;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.UserStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserStatus status;
    private Set<Role> roles;
}
