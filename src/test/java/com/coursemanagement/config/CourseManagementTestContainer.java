package com.coursemanagement.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class CourseManagementTestContainer extends PostgreSQLContainer<CourseManagementTestContainer> {
    private static final String IMAGE_VERSION = "postgres:15.2-alpine";
    private static CourseManagementTestContainer container;

    private CourseManagementTestContainer() {
        super(IMAGE_VERSION);
    }

    public static CourseManagementTestContainer getInstance() {
        if (container == null) {
            container = new CourseManagementTestContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
