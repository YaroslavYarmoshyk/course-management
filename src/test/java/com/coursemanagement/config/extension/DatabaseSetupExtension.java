package com.coursemanagement.config.extension;

import com.coursemanagement.config.container.DatabaseTestContainer;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetupExtension implements BeforeAllCallback, AfterEachCallback {

    @Override
    public void beforeAll(final ExtensionContext extensionContext) {
        DatabaseTestContainer.container.start();
        updateDataSourceProps(DatabaseTestContainer.container);
    }

    private void updateDataSourceProps(DatabaseTestContainer container) {
        System.setProperty("spring.datasource.url", container.getJdbcUrl());
        System.setProperty("spring.datasource.username", container.getUsername());
        System.setProperty("spring.datasource.password", container.getPassword());
    }

    @Override
    public void afterEach(final ExtensionContext extensionContext) throws Exception {
        final DataSource dataSource = SpringExtension.getApplicationContext(extensionContext).getBean(DataSource.class);
        cleanupDatabaseTables(dataSource);
    }

    private void cleanupDatabaseTables(final DataSource dataSource) throws Exception {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()
        ) {
            final String sql = new String(Files.readAllBytes(Paths.get("src/test/resources/scripts/truncate_tables.sql")));
            statement.execute(sql);
        }
    }
}
