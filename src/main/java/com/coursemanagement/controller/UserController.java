package com.coursemanagement.controller;

import com.coursemanagement.repository.UserRepository;
import com.coursemanagement.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping(value = "/all")
    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }
}
