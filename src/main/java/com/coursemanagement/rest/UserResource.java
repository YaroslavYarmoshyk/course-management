package com.coursemanagement.rest;

import com.coursemanagement.annotation.CurrentUserId;
import com.coursemanagement.model.User;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.coursemanagement.util.Constants.USERS_ENDPOINT;

@RestController
@RequestMapping(USERS_ENDPOINT)
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;

    @GetMapping("/me")
    public User getCurrentUser(@CurrentUserId final Long userId) {
        return userService.getUserById(userId);
    }
}
