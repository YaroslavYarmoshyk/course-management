package com.coursemanagement.config;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseCleanupExtension implements AfterEachCallback {

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
