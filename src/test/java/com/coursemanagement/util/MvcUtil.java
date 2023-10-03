package com.coursemanagement.util;

import com.coursemanagement.model.User;
import com.coursemanagement.security.model.AuthenticationRequest;
import com.coursemanagement.security.model.AuthenticationResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.coursemanagement.util.Constants.LOGIN_ENDPOINT;
import static com.coursemanagement.util.JsonUtil.asJsonString;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@Component
public class MvcUtil {

    public static ResultActions makeMockMvcRequest(final MockMvc mockMvc,
                                                   final HttpMethod requestType,
                                                   final String endpoint) throws Exception {
        return makeMockMvcRequest(mockMvc, requestType, endpoint, null, null, null);
    }

    public static ResultActions makeMockMvcRequest(final MockMvc mockMvc,
                                                   final HttpMethod requestType,
                                                   final String endpoint,
                                                   final User user) throws Exception {
        return makeMockMvcRequest(mockMvc, requestType, endpoint, null, null, user);
    }

    public static ResultActions makeMockMvcRequest(final MockMvc mockMvc,
                                                   final HttpMethod requestType,
                                                   final String endpoint,
                                                   final Object body) throws Exception {
        return makeMockMvcRequest(mockMvc, requestType, endpoint, null, body, null);
    }

    public static ResultActions makeMockMvcRequest(final MockMvc mockMvc,
                                                   final HttpMethod requestType,
                                                   final String endpoint,
                                                   final Object body,
                                                   final User user) throws Exception {
        return makeMockMvcRequest(mockMvc, requestType, endpoint, null, body, user);
    }

    public static ResultActions makeMockMvcRequest(final MockMvc mockMvc,
                                                   final HttpMethod requestType,
                                                   final String endpoint,
                                                   final Map<String, Object> params) throws Exception {
        return makeMockMvcRequest(mockMvc, requestType, endpoint, params, null, null);
    }

    public static ResultActions makeMockMvcRequest(final MockMvc mockMvc,
                                                   final HttpMethod requestType,
                                                   final String endpoint,
                                                   final Map<String, Object> params,
                                                   final Object body,
                                                   final User user) throws Exception {
        final var requestBuilder = defineRequestBuilder(requestType, endpoint);
        addBodyIfExists(body, requestBuilder);
        addUserIfExists(user, requestBuilder);
        return mockMvc.perform(requestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .params(convertToMultiValueMap(params)));
    }

    private static MockHttpServletRequestBuilder defineRequestBuilder(final HttpMethod requestType, final String endpoint) {
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
        return requestBuilder;
    }

    private static void addBodyIfExists(final Object body, final MockHttpServletRequestBuilder requestBuilder) {
        if (body != null) {
            requestBuilder.content(asJsonString(body));
        }
    }

    private static void addUserIfExists(final User user, final MockHttpServletRequestBuilder requestBuilder) {
        if (user != null) {
            final List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> (GrantedAuthority) () -> "ROLE_" + role.name())
                    .toList();
            requestBuilder.with(jwt().jwt(j -> j.subject(user.getEmail())).authorities(authorities));
        }
    }

    private static MultiValueMap<String, String> convertToMultiValueMap(final Map<String, Object> actualParameters) {
        final LinkedMultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        if (Objects.isNull(actualParameters)) {
            return multiValueMap;
        }
        actualParameters.forEach((key, value) -> multiValueMap.add(key, String.valueOf(value)));
        return multiValueMap;
    }

    public static <T> T makeCall(final TestRestTemplate restTemplate,
                                 final HttpMethod httpMethod,
                                 final String url,
                                 final Class<T> responseType) {
        return makeCall(restTemplate, httpMethod, url, null, responseType, null);
    }

    public static <T> T makeCall(final TestRestTemplate restTemplate,
                                 final HttpMethod httpMethod,
                                 final String url,
                                 final Class<T> responseType,
                                 final User user) {
        return makeCall(restTemplate, httpMethod, url, null, responseType, new AuthenticationRequest(user));
    }

    public static <T, R> T makeCall(final TestRestTemplate restTemplate,
                                    final HttpMethod httpMethod,
                                    final String url,
                                    final R body,
                                    final Class<T> responseType) {
        return makeCall(restTemplate, httpMethod, url, body, responseType, null);
    }


    public static <T, R> T makeCall(final TestRestTemplate restTemplate,
                                    final HttpMethod httpMethod,
                                    final String url,
                                    final R body,
                                    final Class<T> responseType,
                                    final AuthenticationRequest authenticationRequest) {
        final HttpEntity<R> httpEntity = getHttpEntity(restTemplate, body, authenticationRequest);
        return restTemplate.exchange(url, httpMethod, httpEntity, responseType).getBody();
    }

    private static <R> HttpEntity<R> getHttpEntity(final TestRestTemplate restTemplate, final R body, final AuthenticationRequest authenticationRequest) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (Objects.nonNull(authenticationRequest)) {
            final var response = makeCall(restTemplate, POST, LOGIN_ENDPOINT, authenticationRequest, AuthenticationResponse.class);
            headers.set("Authorization", "Bearer " + response.token());
        }
        return new HttpEntity<>(body, headers);
    }
}
