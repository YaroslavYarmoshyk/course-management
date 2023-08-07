package com.coursemanagement.service;

public interface CourseAssociationService {

    boolean isUserAssociatedWithCourse(final Long userId, final Long courseCode);
}
