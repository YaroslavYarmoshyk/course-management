package com.coursemanagement.service.impl;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.User;
import com.coursemanagement.model.mapper.UserMapper;
import com.coursemanagement.repository.UserRepository;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User findByEmail(final String email) {
        final Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if (userEntity.isPresent()) {
            return userMapper.toModel(userEntity.get());
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
        final UserEntity userEntity = userMapper.toEntity(user);
        final UserEntity savedUser = userRepository.save(userEntity);
        return userMapper.toModel(savedUser);
    }

    private boolean isAlreadyExists(final User user) {
        final String email = user.getEmail();
        return userRepository.findByEmail(email).isPresent();
    }
}
