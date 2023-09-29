package com.coursemanagement.integration.resource;

import com.coursemanagement.config.annotation.EnableSecurityConfiguration;
import com.coursemanagement.rest.CourseResource;
import com.coursemanagement.rest.dto.UserCourseDto;
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
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
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

    @Order(1)
    @TestFactory
    @DisplayName("Test user courses endpoint")
    Stream<DynamicTest> testUserCoursesEndpoint() {
        final Long userId = FIRST_STUDENT.getId();
        final Set<UserCourseDto> userCourses = Instancio.ofSet(UserCourseDto.class).create();
        return Stream.of(
                dynamicTest("Test unauthorized access to endpoint",
                        () -> assertUnauthorizedAccess(mockMvc, GET, COURSE_ENDPOINT)),
                dynamicTest("Test valid user course request",
                        () -> {
                            when(userCourseService.getUserCourseSummariesByUserId(userId)).thenReturn(userCourses);
                            makeMockMvcRequest(mockMvc, GET, COURSE_ENDPOINT, FIRST_STUDENT)
                                    .andExpect(responseBody().containsObjectsAsJson(userCourses, UserCourseDto.class));
                        }),
                dynamicTest("Test exception deserialization", () -> assertExceptionDeserialization(
                                mockMvc,
                                GET,
                                COURSE_ENDPOINT,
                                userCourseService.getUserCourseSummariesByUserId(any())
                        )
                )
        );
    }
}