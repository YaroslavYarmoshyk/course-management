package com.coursemanagement.util;


import static com.coursemanagement.util.Constants.*;

public final class BaseEndpoints {

    public static final String RESET_PASSWORD_REQUEST_ENDPOINT = RESET_PASSWORD_ENDPOINT + "/request";
    public static final String RESET_PASSWORD_CONFIRMATION_ENDPOINT = RESET_PASSWORD_ENDPOINT + "/confirm";
    public static final String ROLE_ASSIGNMENT_ENDPOINT = ADMIN_RESOURCE_ENDPOINT + "/assign-role";
    public static final String COURSE_ASSIGNMENT_ENDPOINT = COURSE_MANAGEMENT_ENDPOINT + "/assign-instructor";
    public static final String COURSE_ENROLLMENT_ENDPOINT = COURSE_MANAGEMENT_ENDPOINT + "/enrollments";
    public static final String COURSE_COMPLETION_ENDPOINT = COURSE_MANAGEMENT_ENDPOINT + "/complete";
    public static final String FEEDBACK_SUBMISSION_ENDPOINT = COURSE_MANAGEMENT_ENDPOINT + "/provide-feedback";
    public static final String HOMEWORK_UPLOAD_ENDPOINT = HOMEWORK_ENDPOINT + "/upload";
    public static final String HOMEWORK_DOWNLOAD_ENDPOINT = HOMEWORK_ENDPOINT + "/download";
    public static final String MARK_ASSIGNMENT_ENDPOINT = LESSONS_ENDPOINT + "/assign-mark";
}
