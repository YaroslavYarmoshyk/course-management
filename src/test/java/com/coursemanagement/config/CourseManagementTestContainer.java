package com.coursemanagement.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class CourseManagementTestContainer extends PostgreSQLContainer<CourseManagementTestContainer> {
    private static final String IMAGE_VERSION = "postgres:15.2-alpine";
    private static final String DATABASE_NAME = "course-management-test-db";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "strong-password";

    private CourseManagementTestContainer() {
        super(IMAGE_VERSION);
    }

    public static CourseManagementTestContainer container = new CourseManagementTestContainer()
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .withInitScript("scripts/init_tables_with_test_data.sql");
}
