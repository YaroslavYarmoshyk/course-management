package com.coursemanagement.security.controller;

import com.coursemanagement.security.dto.AuthenticationRequestDto;
import com.coursemanagement.security.dto.AuthenticationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/verification")
@RequiredArgsConstructor
public class AuthenticationController {

    @PostMapping(value = "/register")
    public AuthenticationResponseDto register(@RequestBody AuthenticationRequestDto authenticationRequest) {

    }

    @PostMapping(value = "/authenticate")
    public AuthenticationResponseDto register(@RequestBody AuthenticationRequestDto authenticationRequest) {

    }
}
