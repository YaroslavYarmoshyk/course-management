package com.coursemanagement.util;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.dto.ErrorDto;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.UserCourseDetailsDto;
import com.coursemanagement.rest.dto.UserLessonDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseBodyMatcherUtils {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<Class<?>, String[]> FIELDS_TO_IGNORE_MAP = new HashMap<>();

    public static ResponseBodyMatcherUtils responseBody() {
        return new ResponseBodyMatcherUtils();
    }

    {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        FIELDS_TO_IGNORE_MAP.put(User.class, new String[]{"password"});
        FIELDS_TO_IGNORE_MAP.put(UserCourseDetailsDto.class, new String[]{
                "courseMark.courseCode",
                "courseMark.studentId",
                "accomplishmentDate",
                "enrollmentDate"
        });
        FIELDS_TO_IGNORE_MAP.put(UserLessonDto.class, new String[]{"lessonContent.lessonId"});
    }

    public <T> ResultMatcher containsObjectAsJson(final T expectedObject, final Class<T> targetClass) {
        return mvcResult -> {
            final String json = mvcResult.getResponse().getContentAsString();
            final T actualObject = objectMapper.readValue(json, targetClass);
            assertThat(actualObject)
                    .usingRecursiveComparison()
                    .ignoringFields(FIELDS_TO_IGNORE_MAP.getOrDefault(targetClass, new String[]{""}))
                    .isEqualTo(expectedObject);
        };
    }

    public <T> ResultMatcher containsObjectsAsJson(final Collection<T> expectedObjects, final Class<T> targetClass) {
        return mvcResult -> {
            final String json = mvcResult.getResponse().getContentAsString();
            final Collection<T> actualObjects = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(Collection.class, targetClass));
            assertThat(actualObjects)
                    .usingRecursiveFieldByFieldElementComparator()
                    .usingRecursiveFieldByFieldElementComparatorIgnoringFields(FIELDS_TO_IGNORE_MAP.getOrDefault(targetClass, new String[]{""}))
                    .isEqualTo(expectedObjects);
        };
    }

    public ResultMatcher equalToString(final String expectedString) {
        return mvcResult -> assertEquals(expectedString, mvcResult.getResponse().getContentAsString());
    }

    public ResultMatcher containsSystemException(final SystemException systemException) {
        return mvcResult -> {
            final String json = mvcResult.getResponse().getContentAsString();
            final ErrorDto actualObject = objectMapper.readValue(json, ErrorDto.class);
            final MockHttpServletRequest request = mvcResult.getRequest();
            final ErrorDto expectedObject = new ErrorDto(systemException, request);
            assertThat(actualObject)
                    .usingRecursiveComparison()
                    .ignoringFieldsOfTypes(Date.class)
                    .isEqualTo(expectedObject);
        };
    }
}
