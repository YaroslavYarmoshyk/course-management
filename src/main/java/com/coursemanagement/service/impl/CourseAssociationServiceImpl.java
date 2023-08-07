package com.coursemanagement.service.impl;

import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.service.CourseAssociationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseAssociationServiceImpl implements CourseAssociationService {
    private final CourseRepository courseRepository;

    @Override
    public boolean isUserAssociatedWithCourse(final Long userId, final Long courseCode) {
        return courseRepository.existsByUserCoursesUserIdAndCode(userId, courseCode);
    }
}
