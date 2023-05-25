package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.model.User;
import com.coursemanagement.model.mapper.UserMapper;
import com.coursemanagement.repository.RoleRepository;
import com.coursemanagement.repository.UserRepository;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.service.ConfirmationTokenService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserMapper userMapper;

    @Override
    public User findByEmail(final String email) {
        final Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if (userEntity.isPresent()) {
            return userMapper.entityToModel(userEntity.get());
        }
        throw new SystemException("User by email " + email + " not found", SystemErrorCode.BAD_REQUEST);
    }

    @Override
    public User findById(final Long id) {
        final Optional<UserEntity> userEntity = userRepository.findById(id);
        if (userEntity.isPresent()) {
            return userMapper.entityToModel(userEntity.get());
        }
        throw new SystemException("User by id " + id + " not found", SystemErrorCode.BAD_REQUEST);
    }

    @Override
    @Transactional
    public void confirmUserByEmailToken(final String token) {
        final String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        final ConfirmationToken confirmationToken = confirmationTokenService.confirmToken(encodedToken, TokenType.EMAIL_CONFIRMATION);
        final Long userId = confirmationToken.userId();
        final UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new SystemException("Cannot find user by id: " + userId, SystemErrorCode.INTERNAL_SERVER_ERROR));
        userEntity.setStatus(UserStatus.ACTIVE);
        userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public User save(final User user) {
        final UserEntity userEntity = userMapper.modelToEntity(user);
        final Set<RoleEntity> roleEntities = Optional.ofNullable(user.getRoles())
                .map(roleRepository::findAllByNameIn)
                .orElse(new HashSet<>());
        userEntity.setRoles(roleEntities);
        final UserEntity savedUser = userRepository.save(userEntity);
        return userMapper.entityToModel(savedUser);
    }

    @Override
    public boolean isEmailAlreadyRegistered(final String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public UserDto assignRole(final RoleAssignmentDto roleAssignmentDto) {
        final User user = findById(roleAssignmentDto.userId());
        user.getRoles().addAll(roleAssignmentDto.roles());
        final User savedUser = save(user);
        return userMapper.modelToDto(savedUser);
    }
}
