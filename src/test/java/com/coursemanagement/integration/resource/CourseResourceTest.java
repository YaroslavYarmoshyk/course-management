package com.coursemanagement.integration.resource;

import com.coursemanagement.config.annotation.EnableSecurityConfiguration;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.rest.CourseResource;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;
import com.coursemanagement.rest.dto.UserCourseDto;
import com.coursemanagement.rest.dto.UserDto;
import com.coursemanagement.rest.dto.UserLessonDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.UserCourseService;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.stream.Stream;

import static com.coursemanagement.config.ResponseBodyMatchers.responseBody;
import static com.coursemanagement.util.AssertionsUtils.assertExceptionDeserialization;
import static com.coursemanagement.util.AssertionsUtils.assertUnauthorizedAccess;
import static com.coursemanagement.util.Constants.COURSE_ENDPOINT;
import static com.coursemanagement.util.MvcUtil.makeMockMvcRequest;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static com.coursemanagement.util.TestDataUtils.INSTRUCTOR;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

@ExtendWith(value = InstancioExtension.class)
@WebMvcTest(value = CourseResource.class)
@EnableSecurityConfiguration
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class CourseResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CourseService courseService;
    @MockBean
    private UserCourseService userCourseService;
    @MockBean
    private LessonService lessonService;
    private static final Long COURSE_CODE = 112L;

    @Order(1)
    @TestFactory
    @DisplayName("Test user courses endpoint")
    Stream<DynamicTest> testUserCoursesEndpoint() {
        final Set<UserCourseDto> userCourses = Instancio.ofSet(UserCourseDto.class).create();
        return Stream.of(
                dynamicTest("Test unauthorized access to endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, COURSE_ENDPOINT)),
                dynamicTest("Test valid user course request",
                        () -> {
                            when(userCourseService.getUserCourseSummariesByUserId(FIRST_STUDENT.getId())).thenReturn(userCourses);
                            makeMockMvcRequest(mockMvc, GET, COURSE_ENDPOINT, FIRST_STUDENT)
                                    .andExpect(responseBody().containsObjectsAsJson(userCourses, UserCourseDto.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                COURSE_ENDPOINT,
                                userCourseService.getUserCourseSummariesByUserId(anyLong())
                        )
                )
        );
    }

    @Order(2)
    @TestFactory
    @DisplayName("Test user course details endpoint")
    Stream<DynamicTest> testUserCoursesByCourseCodeEndpoint() {
        final String courseDetailsEndpoint = String.format("%s%s%d", COURSE_ENDPOINT, "/", COURSE_CODE);
        final UserCourseDetailsDto userCourseDetails = Instancio.create(UserCourseDetailsDto.class);
        return Stream.of(
                dynamicTest("Test unauthorized access to endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, courseDetailsEndpoint)),
                dynamicTest("Test valid user course details request",
                        () -> {
                            when(userCourseService.getUserCourseDetails(FIRST_STUDENT.getId(), COURSE_CODE)).thenReturn(userCourseDetails);
                            makeMockMvcRequest(mockMvc, GET, courseDetailsEndpoint, FIRST_STUDENT)
                                    .andExpect(responseBody().containsObjectAsJson(userCourseDetails, UserCourseDetailsDto.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                courseDetailsEndpoint,
                                userCourseService.getUserCourseDetails(anyLong(), anyLong())
                        )
                )
        );
    }

    @Order(3)
    @TestFactory
    @DisplayName("Test students per course endpoint")
    Stream<DynamicTest> testStudentsPerCourseEndpoint() {
        final String studentsPerCourseEndpoint = String.format("%s%d%s", COURSE_ENDPOINT + "/", COURSE_CODE, "/students");
        final Set<UserDto> students = Instancio.ofSet(UserDto.class).create();
        return Stream.of(
                dynamicTest("Test unauthorized access to endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, studentsPerCourseEndpoint, Role.INSTRUCTOR)),
                dynamicTest("Test valid students per course request",
                        () -> {
                            when(userCourseService.getStudentsByCourseCode(COURSE_CODE)).thenReturn(students);
                            makeMockMvcRequest(mockMvc, GET, studentsPerCourseEndpoint, INSTRUCTOR)
                                    .andExpect(responseBody().containsObjectsAsJson(students, UserDto.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                studentsPerCourseEndpoint,
                                userCourseService.getStudentsByCourseCode(COURSE_CODE)
                        )
                )
        );
    }

    @Order(4)
    @TestFactory
    @DisplayName("Test user lessons per course endpoint")
    Stream<DynamicTest> testUserLessonPerCourseEndpoint() {
        final String userLessonEndpoint = String.format("%s%d%s", COURSE_ENDPOINT + "/", COURSE_CODE, "/lessons");
        final Set<UserLessonDto> userLessons = Instancio.ofSet(UserLessonDto.class).create();
        return Stream.of(
                dynamicTest("Test unauthorized access to endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, userLessonEndpoint, FIRST_STUDENT)),
                dynamicTest("Test valid user lessons per course request",
                        () -> {
                            when(lessonService.getUserLessonsWithContentPerCourse(FIRST_STUDENT.getId(), COURSE_CODE)).thenReturn(userLessons);
                            makeMockMvcRequest(mockMvc, GET, userLessonEndpoint, FIRST_STUDENT)
                                    .andExpect(responseBody().containsObjectsAsJson(userLessons, UserLessonDto.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                userLessonEndpoint,
                                lessonService.getUserLessonsWithContentPerCourse(anyLong(), anyLong())
                        )
                )
        );
    }

    @Order(5)
    @TestFactory
    @DisplayName("Test user course final mark endpoint")
    Stream<DynamicTest> testUserCourseFinalMarkEndpoint() {
        final String courseMarkEndpoint = String.format("%s%d%s", COURSE_ENDPOINT + "/", COURSE_CODE, "/final-mark");
        final CourseMark courseMark = Instancio.create(CourseMark.class);
        return Stream.of(
                dynamicTest("Test unauthorized access to endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, courseMarkEndpoint, FIRST_STUDENT)),
                dynamicTest("Test valid final course mark request",
                        () -> {
                            when(courseService.getStudentCourseFinalMark(FIRST_STUDENT.getId(), COURSE_CODE)).thenReturn(courseMark);
                            makeMockMvcRequest(mockMvc, GET, courseMarkEndpoint, FIRST_STUDENT)
                                    .andExpect(responseBody().containsObjectAsJson(courseMark, CourseMark.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                courseMarkEndpoint,
                                courseService.getStudentCourseFinalMark(anyLong(), anyLong())
                        )
                )
        );
    }
}