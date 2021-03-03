package info.jerrinot.portablemapstore.impl;

import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class SimpleConnectionProviderTest {
    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa");

    @Test
    public void testConnection() throws SQLException {
        var provider = new SimpleConnectionProvider(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword());
        var conn = provider.getConnection();
        assertNotNull(conn);
    }

}