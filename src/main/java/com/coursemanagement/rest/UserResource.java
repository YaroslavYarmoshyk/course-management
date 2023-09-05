package com.coursemanagement.rest;

import com.coursemanagement.annotation.CurrentUserId;
import com.coursemanagement.model.User;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;

    @GetMapping("/me")
    public User getCurrentUser(@CurrentUserId final Long userId) {
        return userService.getUserById(userId);
    }
}
