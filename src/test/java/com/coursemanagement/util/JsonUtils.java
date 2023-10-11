package com.coursemanagement.util;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsString(obj);
        } catch (final Exception e) {
            throw new SystemException("Cannot convert object: " + obj.toString() + " to string", SystemErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
