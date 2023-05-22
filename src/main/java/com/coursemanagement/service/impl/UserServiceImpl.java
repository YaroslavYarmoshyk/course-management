package com.coursemanagement.service.impl;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.UserRepository;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public User findByEmail(final String email) {
        final Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if (userEntity.isPresent()) {
            return modelMapper.map(userEntity, User.class);
        }
        throw new SystemException("User by email " + email + " not found", HttpStatus.BAD_REQUEST);
    }

    @Override
    public User save(final User user) {
        if (isAlreadyExists(user)) {
            throw new SystemException(
                    "User with email " + user.getEmail() + " already exists",
                    HttpStatus.BAD_REQUEST
            );
        }
        final UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        final UserEntity savedUser = userRepository.save(userEntity);
        return modelMapper.map(savedUser, User.class);
    }

    private boolean isAlreadyExists(final User user) {
        final String email = user.getEmail();
        return userRepository.findByEmail(email).isPresent();
    }
}
