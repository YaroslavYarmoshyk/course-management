package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.exception.SystemException;
import com.coursemanagement.exception.enumeration.SystemErrorCode;
import com.coursemanagement.model.User;
import com.coursemanagement.repository.RoleRepository;
import com.coursemanagement.repository.UserRepository;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper mapper;

    @Override
    public User resolveCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getName)
                .map(this::getUserByEmail)
                .orElseThrow(() -> new SystemException("Cannot resolve current user, the user is unauthorized", SystemErrorCode.UNAUTHORIZED));
    }

    @Override
    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email)
                .map(entity -> mapper.map(entity, User.class))
                .orElseThrow(() -> new SystemException("User by email " + email + " not found", SystemErrorCode.BAD_REQUEST));
    }

    @Override
    public User getUserById(final Long id) {
        return userRepository.findById(id)
                .map(entity -> mapper.map(entity, User.class))
                .orElseThrow(() -> new SystemException("User not found", SystemErrorCode.BAD_REQUEST));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userEntity -> mapper.map(userEntity, User.class))
                .sorted(Comparator.comparingLong(User::getId))
                .toList();
    }

    @Override
    public User activateById(final Long userId) {
        final User userById = getUserById(userId);
        userById.setStatus(UserStatus.ACTIVE);
        return save(userById);
    }

    @Override
    public User save(final User user) {
        final UserEntity userEntity = mapper.map(user, UserEntity.class);
        final Set<RoleEntity> roleEntities = Optional.ofNullable(user.getRoles())
                .map(roleRepository::findAllByRoleIn)
                .orElse(new HashSet<>());
        userEntity.setRoles(roleEntities);
        final UserEntity savedUser = userRepository.save(userEntity);
        return mapper.map(savedUser, User.class);
    }
}
