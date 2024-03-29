package com.coursemanagement.service;

import com.coursemanagement.model.Course;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.User;

import java.util.Collection;
import java.util.Set;

public interface CourseService {

    Course getCourseByCode(final Long code);

    Set<Course> getCoursesByCodes(final Collection<Long> codes);

    void addUserToCourses(final User student, final Collection<Long> courseCodes);

    CourseMark getStudentCourseFinalMark(final Long studentId, final Long courseCode);
}
