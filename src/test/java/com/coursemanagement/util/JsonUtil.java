package com.coursemanagement.util;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (final Exception e) {
            throw new SystemException("Cannot convert object: " + obj.toString() + " to string", SystemErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
