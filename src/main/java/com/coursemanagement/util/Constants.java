package com.coursemanagement.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Constants {
    public static final String ROLES_CLAIM = "roles";
    public static final String AUTHENTICATION_ENDPOINT = "/api/v1/authentication";
    public static final String REGISTRATION_ENDPOINT = AUTHENTICATION_ENDPOINT + "/register";
    public static final String LOGIN_ENDPOINT = AUTHENTICATION_ENDPOINT + "/login";
    public static final String RESET_PASSWORD_ENDPOINT = "/api/v1/reset-password";
    public static final String ADMIN_RESOURCE_ENDPOINT = "/api/v1/admin";
    public static final String COURSE_MANAGEMENT_ENDPOINT = "/api/v1/course-management";
    public static final String COURSES_ENDPOINT = "/api/v1/courses";
    public static final String LESSONS_ENDPOINT = "/api/v1/lessons";
    public static final String HOMEWORK_ENDPOINT = "/api/v1/homework";
    public static final String USERS_ENDPOINT = "/api/v1/users";
    public static final String ERROR_ENDPOINT = "/error";
    public static final String FAVICON = "/favicon.ico";

    public static final String EMAIL_CONFIRMATION_ENDPOINT = "/confirm-email";
    public static final String TOKEN_CONFIRMATION_ENDPOINT_PARAMETER = "token";

    public static final String EMAIL_CONFIRMATION_TEMPLATE_PATH = "/templates/email-confirmation.html";
    public static final String RESET_PASSWORD_CONFIRMATION_TEMPLATE_PATH = "/templates/reset-password-confirmation.html";

    public static final String EMAIL_CONFIRMATION_SUBJECT = "Confirm your email";
    public static final String RESET_PASSWORD_CONFIRMATION_SUBJECT = "Password Reset Request";

    public static final String EMAIL_CONFIRMATION_URL = AUTHENTICATION_ENDPOINT + EMAIL_CONFIRMATION_ENDPOINT + "?" + TOKEN_CONFIRMATION_ENDPOINT_PARAMETER + "=";
    public static final String RESET_PASSWORD_CONFIRMATION_URL = RESET_PASSWORD_ENDPOINT + "/confirm" + "?" + TOKEN_CONFIRMATION_ENDPOINT_PARAMETER + "=";

    public static final String EMAIL_SENDER = "course-administration";

    public static final int MARK_CONVERTER_SCALE = 0;
    public static final int MARK_ROUNDING_SCALE = 2;
    public static final RoundingMode MARK_ROUNDING_MODE = RoundingMode.HALF_UP;

    public static final double ZERO_MARK_VALUE = 0.0;

    public static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
}
