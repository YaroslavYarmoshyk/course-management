package com.coursemanagement.rest;

import com.coursemanagement.annotation.AdminAccessLevel;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;
import com.coursemanagement.rest.dto.UserCourseDto;
import com.coursemanagement.rest.dto.UserLessonDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.RoleManagementService;
import com.coursemanagement.service.UserCourseService;
import com.coursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/admin")
@RequiredArgsConstructor
@AdminAccessLevel
public class AdminResource {
    private final RoleManagementService roleManagementService;
    private final UserService userService;
    private final UserCourseService userCourseService;
    private final CourseService courseService;
    private final LessonService lessonService;

    @PostMapping(value = "/assign-role")
    public User assignRoleToUser(@RequestBody final RoleAssignmentDto roleAssignmentDto) {
        return roleManagementService.assignRoleToUser(roleAssignmentDto);
    }

    @GetMapping("/users")
    public Set<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{user-id}")
    public User getUserById(@PathVariable(value = "user-id") final Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/users/{user-id}/courses")
    public Set<UserCourseDto> getUserCourses(@PathVariable(value = "user-id") final Long userId) {
        return userCourseService.getUserCourseSummariesByUserId(userId);
    }

    @GetMapping(value = "/users/{user-id}/courses/{course-code}")
    public UserCourseDetailsDto getUserCourseDetails(@PathVariable(value = "user-id") final Long userId,
                                                     @PathVariable(value = "course-code") final Long courseCode) {
        return userCourseService.getUserCourseDetails(userId, courseCode);
    }

    @GetMapping(value = "/users/{user-id}/courses/{course-code}/final-mark")
    public CourseMark getStudentCourseMark(@PathVariable(value = "user-id") final Long studentId,
                                           @PathVariable(value = "course-code") final Long courseCode) {
        return courseService.getStudentCourseFinalMark(studentId, courseCode);
    }

    @GetMapping(value = "/users/{user-id}/courses/{course-code}/lessons")
    public Set<UserLessonDto> getStudentLessonsPerCourse(@PathVariable(value = "user-id") final Long studentId,
                                                         @PathVariable(value = "course-code") final Long courseCode) {
        return lessonService.getUserLessonsWithContentPerCourse(studentId, courseCode);
    }
}
