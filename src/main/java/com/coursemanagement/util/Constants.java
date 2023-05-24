package com.coursemanagement.util;

public final class Constants {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final int BEARER_TOKEN_START_INDEX = 7;
    public static final String AUTHENTICATION_ENDPOINT = "/api/authentication";
    public static final String TOKEN_CONFIRMATION_ENDPOINT = "/confirm";
    public static final String TOKEN_CONFIRMATION_ENDPOINT_PARAMETER = "token";

    public static final String EMAIL_CONFIRMATION_TEMPLATE_PATH = "src/main/resources/templates/email-confirmation.html";
    public static final String EMAIL_CONFIRMATION_SUBJECT = "Confirm your email";
    public static final String EMAIL_CONFIRMATION_URL = AUTHENTICATION_ENDPOINT + TOKEN_CONFIRMATION_ENDPOINT + "?" + TOKEN_CONFIRMATION_ENDPOINT_PARAMETER + "=";
    public static final String EMAIL_SENDER = "course-administration";
}
