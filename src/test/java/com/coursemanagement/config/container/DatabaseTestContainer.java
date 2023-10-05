package com.coursemanagement.config.container;

import org.testcontainers.containers.PostgreSQLContainer;

public class DatabaseTestContainer extends PostgreSQLContainer<DatabaseTestContainer> {
    private static final String IMAGE_VERSION = "postgres:15.2-alpine";
    private static final String DATABASE_NAME = "course-management-test-db";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "strong-password";

    private DatabaseTestContainer() {
        super(IMAGE_VERSION);
    }

    public static DatabaseTestContainer container = new DatabaseTestContainer()
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .withInitScript("scripts/init_tables.sql");
}
