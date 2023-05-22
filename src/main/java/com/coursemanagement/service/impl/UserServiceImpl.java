package com.coursemanagement.service.impl;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.User;
import com.coursemanagement.model.mapper.UserMapper;
import com.coursemanagement.repository.RoleRepository;
import com.coursemanagement.repository.UserRepository;
import com.coursemanagement.repository.entity.RoleEntity;
import com.coursemanagement.repository.entity.UserEntity;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
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
    @Transactional
    public User save(final User user) {
        final UserEntity userEntity = userMapper.toEntity(user);
        setRoles(user, userEntity);
        final UserEntity savedUser = userRepository.save(userEntity);
        return userMapper.toModel(savedUser);
    }

    @Override
    public boolean isEmailAlreadyRegistered(final String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private void setRoles(final User user, final UserEntity userEntity) {
        final Set<RoleEntity> roleEntities = Optional.ofNullable(user.getRoles())
                .map(roleRepository::findAllByNameIn)
                .orElse(new HashSet<>());
        userEntity.setRoles(roleEntities);
    }
}
