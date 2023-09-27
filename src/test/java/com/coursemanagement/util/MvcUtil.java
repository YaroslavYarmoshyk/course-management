package com.coursemanagement.util;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.Objects;

import static com.coursemanagement.util.JsonUtil.asJsonString;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class MvcUtil {

    public static ResultActions makeMockMvcRequest(final MockMvc mockMvc,
                                                   final HttpMethod requestType,
                                                   final String endpoint) throws Exception {
        return makeMockMvcRequest(mockMvc, requestType, endpoint, null, null);
    }

    public static ResultActions makeMockMvcRequest(final MockMvc mockMvc,
                                                   final HttpMethod requestType,
                                                   final String endpoint,
                                                   final Object body) throws Exception {
        return makeMockMvcRequest(mockMvc, requestType, endpoint, null, body);
    }

    public static ResultActions makeMockMvcRequest(final MockMvc mockMvc,
                                                   final HttpMethod requestType,
                                                   final String endpoint,
                                                   final Map<String, Object> params) throws Exception {
        return makeMockMvcRequest(mockMvc, requestType, endpoint, params, null);
    }

    public static ResultActions makeMockMvcRequest(final MockMvc mockMvc,
                                                   final HttpMethod requestType,
                                                   final String endpoint,
                                                   final Map<String, Object> params,
                                                   final Object body) throws Exception {
        MockHttpServletRequestBuilder requestBuilder;
        if (POST.equals(requestType)) {
            requestBuilder = post(endpoint);
        } else if (GET.equals(requestType)) {
            requestBuilder = get(endpoint);
        } else if (PUT.equals(requestType)) {
            requestBuilder = put(endpoint);
        } else {
            throw new IllegalArgumentException("Unsupported HTTP request type: " + requestType);
        }

        if (body != null) {
            requestBuilder.content(asJsonString(body));
        }

        return mockMvc.perform(requestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .params(convertToMultiValueMap(params)));
    }

    private static MultiValueMap<String, String> convertToMultiValueMap(final Map<String, Object> actualParameters) {
        final LinkedMultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        if (Objects.isNull(actualParameters)) {
            return multiValueMap;
        }
        actualParameters.forEach((key, value) -> multiValueMap.add(key, String.valueOf(value)));
        return multiValueMap;
    }
}
