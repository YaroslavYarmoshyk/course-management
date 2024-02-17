package com.coursemanagement.unit.resource;

import com.coursemanagement.config.annotation.SecuredResourceTest;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.CourseMark;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.AdminResource;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;
import com.coursemanagement.rest.dto.UserCourseDto;
import com.coursemanagement.rest.dto.UserLessonDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.RoleManagementService;
import com.coursemanagement.service.UserCourseService;
import com.coursemanagement.service.UserService;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.coursemanagement.util.ResponseBodyMatcherUtils.responseBody;
import static com.coursemanagement.util.AssertionsUtils.assertExceptionDeserialization;
import static com.coursemanagement.util.AssertionsUtils.assertUnauthorizedAccess;
import static com.coursemanagement.util.Constants.ADMIN_RESOURCE_ENDPOINT;
import static com.coursemanagement.util.MvcUtils.makeMockMvcRequest;
import static com.coursemanagement.util.TestDataUtils.*;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecuredResourceTest(value = AdminResource.class)
class AdminResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RoleManagementService roleManagementService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserCourseService userCourseService;
    @MockBean
    private CourseService courseService;
    @MockBean
    private LessonService lessonService;

    private static final Long USER_ID = FIRST_STUDENT.getId();
    private static final Long COURSE_CODE = 112L;

    @Order(1)
    @TestFactory
    @DisplayName("Test admins assign role endpoint")
    Stream<DynamicTest> testAdminAssignRoleEndpoint() {
        final String assignRoleEndpoint = ADMIN_RESOURCE_ENDPOINT + "/assign-role";
        final RoleAssignmentDto roleAssignmentDto = Instancio.create(RoleAssignmentDto.class);
        return Stream.of(
                dynamicTest("Test empty body request",
                        () -> makeMockMvcRequest(mockMvc, POST, assignRoleEndpoint, ADMIN).andExpect(status().isBadRequest())),
                dynamicTest("Test unauthorized access to admins endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, POST, assignRoleEndpoint, roleAssignmentDto, Role.ADMIN)),
                dynamicTest("Test valid role assigment request",
                        () -> {
                            when(roleManagementService.assignRoleToUser(roleAssignmentDto)).thenReturn(FIRST_STUDENT);
                            makeMockMvcRequest(mockMvc, POST, assignRoleEndpoint, roleAssignmentDto, ADMIN)
                                    .andExpect(responseBody().containsObjectAsJson(FIRST_STUDENT, User.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                POST,
                                assignRoleEndpoint,
                                roleAssignmentDto,
                                roleManagementService.assignRoleToUser(roleAssignmentDto)
                        )
                )
        );
    }

    @Order(2)
    @TestFactory
    @DisplayName("Test admins all users endpoint")
    Stream<DynamicTest> testAdminAllUsersEndpoint() {
        final String allUsersEndpoint = ADMIN_RESOURCE_ENDPOINT + "/users";
        final List<User> users = Instancio.ofList(USER_TEST_MODEL).create();
        return Stream.of(
                dynamicTest("Test unauthorized access to admins endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, allUsersEndpoint, Role.ADMIN)),
                dynamicTest("Test valid get all users request",
                        () -> {
                            when(userService.getAllUsers()).thenReturn(users);
                            makeMockMvcRequest(mockMvc, GET, allUsersEndpoint, ADMIN)
                                    .andExpect(responseBody().containsObjectsAsJson(users, User.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                allUsersEndpoint,
                                userService.getAllUsers()
                        )
                )
        );
    }

    @Order(3)
    @TestFactory
    @DisplayName("Test admins user by id endpoint")
    Stream<DynamicTest> testAdminUserByIdEndpoint() {
        final String userByIdEndpoint = String.format("%s%s%d", ADMIN_RESOURCE_ENDPOINT, "/users/", USER_ID);
        return Stream.of(
                dynamicTest("Test unauthorized access to admins endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, userByIdEndpoint, Role.ADMIN)),
                dynamicTest("Test valid get user by id request",
                        () -> {
                            when(userService.getUserById(USER_ID)).thenReturn(FIRST_STUDENT);
                            makeMockMvcRequest(mockMvc, GET, userByIdEndpoint, ADMIN)
                                    .andExpect(responseBody().containsObjectAsJson(FIRST_STUDENT, User.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                userByIdEndpoint,
                                userService.getUserById(USER_ID)
                        )
                )
        );
    }

    @Order(4)
    @TestFactory
    @DisplayName("Test admins user courses by user id endpoint")
    Stream<DynamicTest> testAdminUserCoursesEndpoint() {
        final String userCoursesEndpoint = String.format("%s%s%d%s", ADMIN_RESOURCE_ENDPOINT, "/users/", USER_ID, "/courses");
        final Set<UserCourseDto> userCourses = Instancio.ofSet(UserCourseDto.class).create();
        return Stream.of(
                dynamicTest("Test unauthorized access to admins endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, userCoursesEndpoint, Role.ADMIN)),
                dynamicTest("Test valid get user courses by user id request",
                        () -> {
                            when(userCourseService.getUserCourseSummariesByUserId(USER_ID)).thenReturn(userCourses);
                            makeMockMvcRequest(mockMvc, GET, userCoursesEndpoint, ADMIN)
                                    .andExpect(responseBody().containsObjectsAsJson(userCourses, UserCourseDto.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                userCoursesEndpoint,
                                userCourseService.getUserCourseSummariesByUserId(USER_ID)
                        )
                )
        );
    }

    @Order(5)
    @TestFactory
    @DisplayName("Test admins user course details by user id and course code endpoint")
    Stream<DynamicTest> testAdminUserCourseDetailsEndpoint() {
        final String userCourseDetailsEndpoint = String.format("%s%s%d%s%d", ADMIN_RESOURCE_ENDPOINT, "/users/", USER_ID, "/courses/", COURSE_CODE);
        final UserCourseDetailsDto userCourseDetails = Instancio.of(UserCourseDetailsDto.class)
                .set(field(UserCourseDetailsDto::courseCode), COURSE_CODE)
                .create();
        return Stream.of(
                dynamicTest("Test unauthorized access to admins endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, userCourseDetailsEndpoint, Role.ADMIN)),
                dynamicTest("Test valid get user course details request",
                        () -> {
                            when(userCourseService.getUserCourseDetails(USER_ID, COURSE_CODE)).thenReturn(userCourseDetails);
                            makeMockMvcRequest(mockMvc, GET, userCourseDetailsEndpoint, ADMIN)
                                    .andExpect(responseBody().containsObjectAsJson(userCourseDetails, UserCourseDetailsDto.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                userCourseDetailsEndpoint,
                                userCourseService.getUserCourseDetails(USER_ID, COURSE_CODE)
                        )
                )
        );
    }

    @Order(6)
    @TestFactory
    @DisplayName("Test admins student course mark endpoint")
    Stream<DynamicTest> testAdminStudentCourseMarkEndpoint() {
        final String studentCourseMarkEndpoint = String.format("%s%s%d%s%d%s", ADMIN_RESOURCE_ENDPOINT, "/users/", USER_ID, "/courses/", COURSE_CODE, "/final-mark");
        final CourseMark studentCourseMark = Instancio.of(CourseMark.class)
                .set(field(CourseMark::getStudentId), USER_ID)
                .set(field(CourseMark::getCourseCode), COURSE_CODE)
                .create();
        return Stream.of(
                dynamicTest("Test unauthorized access to admins endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, studentCourseMarkEndpoint, Role.ADMIN)),
                dynamicTest("Test valid get student course mark request",
                        () -> {
                            when(courseService.getStudentCourseFinalMark(USER_ID, COURSE_CODE)).thenReturn(studentCourseMark);
                            makeMockMvcRequest(mockMvc, GET, studentCourseMarkEndpoint, ADMIN)
                                    .andExpect(responseBody().containsObjectAsJson(studentCourseMark, CourseMark.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                studentCourseMarkEndpoint,
                                courseService.getStudentCourseFinalMark(USER_ID, COURSE_CODE)
                        )
                )
        );
    }

    @Order(7)
    @TestFactory
    @DisplayName("Test admins student lessons per course endpoint")
    Stream<DynamicTest> testAdminStudentLessonsPerCourseEndpoint() {
        final String studentLessonsEndpoint = String.format("%s%s%d%s%d%s", ADMIN_RESOURCE_ENDPOINT, "/users/", USER_ID, "/courses/", COURSE_CODE, "/lessons");
        final Set<UserLessonDto> studentLessons = Instancio.ofSet(UserLessonDto.class).create();
        return Stream.of(
                dynamicTest("Test unauthorized access to admins endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, studentLessonsEndpoint, Role.ADMIN)),
                dynamicTest("Test valid get student lessons per course request",
                        () -> {
                            when(lessonService.getUserLessonsWithContentPerCourse(USER_ID, COURSE_CODE)).thenReturn(studentLessons);
                            makeMockMvcRequest(mockMvc, GET, studentLessonsEndpoint, ADMIN)
                                    .andExpect(responseBody().containsObjectsAsJson(studentLessons, UserLessonDto.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                studentLessonsEndpoint,
                                lessonService.getUserLessonsWithContentPerCourse(USER_ID, COURSE_CODE)
                        )
                )
        );
    }
}