package com.coursemanagement.util;


import static com.coursemanagement.util.Constants.*;

public final class BaseEndpoints {

    public static final String RESET_PASSWORD_REQUEST_ENDPOINT = RESET_PASSWORD_ENDPOINT + "/request";
    public static final String RESET_PASSWORD_CONFIRMATION_ENDPOINT = RESET_PASSWORD_ENDPOINT + "/confirm";
    public static final String ROLE_ASSIGNMENT_ENDPOINT = ADMIN_RESOURCE_ENDPOINT + "/assign-role";
    public static final String COURSE_ASSIGNMENT_ENDPOINT = COURSE_MANAGEMENT_ENDPOINT + "/assign-instructor";
    public static final String COURSE_ENROLLMENT_ENDPOINT = COURSE_MANAGEMENT_ENDPOINT + "/enrollments";
}
