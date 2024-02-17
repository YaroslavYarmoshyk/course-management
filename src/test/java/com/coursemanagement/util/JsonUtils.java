package com.coursemanagement.util;

import com.coursemanagement.exception.SystemException;
import com.coursemanagement.exception.enumeration.SystemErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Collection;

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

    public static <T> Collection<T> asObjectsCollection(final File file, final Class<T> clazz) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final JavaType type = objectMapper.getTypeFactory().constructCollectionType(Collection.class, clazz);
            return objectMapper.readValue(file, type);
        } catch (final Exception e) {
            throw new SystemException("Cannot convert json: " + file.getPath() + " to collection of: " + clazz, SystemErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
