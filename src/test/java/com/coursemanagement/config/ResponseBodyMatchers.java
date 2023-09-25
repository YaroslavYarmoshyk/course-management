package com.coursemanagement.config;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.dto.ErrorDto;
import com.coursemanagement.model.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Date;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseBodyMatchers {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static ResponseBodyMatchers responseBody() {
        return new ResponseBodyMatchers();
    }

    {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T> ResultMatcher containsObjectAsJson(final Object expectedObject, final Class<T> targetClass) {
        return mvcResult -> {
            final String json = mvcResult.getResponse().getContentAsString();
            final T actualObject = objectMapper.readValue(json, targetClass);
            assertThat(actualObject)
                    .usingRecursiveComparison()
                    .ignoringFields(Objects.equals(targetClass, User.class) ? "password" : "")
                    .isEqualTo(expectedObject);
        };
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
