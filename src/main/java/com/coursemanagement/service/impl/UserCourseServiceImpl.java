package com.coursemanagement.service.impl;

import com.coursemanagement.model.UserCourse;
import com.coursemanagement.repository.UserCourseRepository;
import com.coursemanagement.repository.entity.UserCourseEntity;
import com.coursemanagement.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCourseServiceImpl implements UserCourseService {
    private final UserCourseRepository userCourseRepository;
    private final ModelMapper modelMapper;

    @Override
    public Set<UserCourse> getAllByUserId(final Long userId) {
        final List<UserCourseEntity> userCourseEntities = userCourseRepository.findByUserEntityId(userId);
        return userCourseEntities.stream()
                .map(userCourseEntity -> modelMapper.map(userCourseEntity, UserCourse.class))
                .collect(Collectors.toSet());
    }
}
