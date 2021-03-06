package info.jerrinot.portablemapstore;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.function.Supplier;

enum Databases {
    POSTGRES( """
            CREATE TABLE map (
                id              integer PRIMARY KEY,
                name            varchar(40) NOT NULL,
                lastname        varchar(40) NOT NULL,
                doublecolumn    double precision,
                booleancolumn   boolean
            )""", () -> new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa")),

    MYSQL("""
            CREATE TABLE map (
                id              integer PRIMARY KEY,
                name            varchar(40) NOT NULL,
                lastname        varchar(40) NOT NULL,
                doublecolumn    double,
                booleancolumn   boolean
            )""", () -> new MariaDBContainer("mariadb:10.3.6")
            .withDatabaseName("integration-tests-db")
            .withPassword("sa")
            .withPassword("sa"));

    private final String createTableQuery;
    private final Supplier<JdbcDatabaseContainer> containerSupplier;

    Databases(String createTableQuery, Supplier<JdbcDatabaseContainer> containerSupplier) {
        this.createTableQuery = createTableQuery;
        this.containerSupplier = containerSupplier;
    }

    public String getCreateTableQuery() {
        return createTableQuery;
    }

    public JdbcDatabaseContainer newContainer() {
        return containerSupplier.get();
    }
}
