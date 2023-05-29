package com.coursemanagement.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DatabaseSetupExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(final ExtensionContext extensionContext) {
        CourseManagementTestContainer.container.start();
        updateDataSourceProps(CourseManagementTestContainer.container);
    }

    private void updateDataSourceProps(CourseManagementTestContainer container) {
        System.setProperty("spring.datasource.url", container.getJdbcUrl());
        System.setProperty("spring.datasource.username", container.getUsername());
        System.setProperty("spring.datasource.password", container.getPassword());
    }
}
