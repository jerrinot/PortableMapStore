package info.jerrinot.portablemapstore;

import org.testcontainers.containers.Db2Container;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.function.Supplier;

enum SupportedDatabases {
    POSTGRES( """
            CREATE TABLE map (
                id              integer PRIMARY KEY,
                name            varchar(40) NOT NULL,
                lastname        varchar(40) NOT NULL,
                doublecolumn    double precision,
                booleancolumn   boolean
            )""", () -> new PostgreSQLContainer<>("postgres:11.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa")),

    MARIADB("""
            CREATE TABLE map (
                id              integer PRIMARY KEY,
                name            varchar(40) NOT NULL,
                lastname        varchar(40) NOT NULL,
                doublecolumn    double,
                booleancolumn   boolean
            )""", () -> new MariaDBContainer<>("mariadb:10.3.6")
            .withDatabaseName("integration-tests-db")
            .withPassword("sa")
            .withUsername("sa")),

    DB2("""
            CREATE TABLE map (
                id              integer PRIMARY KEY NOT NULL,
                name            varchar(40) NOT NULL,
                lastname        varchar(40) NOT NULL,
                doublecolumn    double,
                booleancolumn   boolean
            )""", () -> new Db2Container("ibmcom/db2:11.5.0.0a")
            .acceptLicense()
            .withPassword("sa")
            .withUsername("sa")),

    MSSQL("""
            CREATE TABLE map (
                id              int PRIMARY KEY,
                name            varchar(40) NOT NULL,
                lastname        varchar(40) NOT NULL,
                doublecolumn    float,
                booleancolumn   bit
            )""", () -> new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2017-CU12")
            .acceptLicense()
    );

    private final String createTableQuery;
    private final Supplier<JdbcDatabaseContainer<?>> containerSupplier;

    SupportedDatabases(String createTableQuery, Supplier<JdbcDatabaseContainer<?>> containerSupplier) {
        this.createTableQuery = createTableQuery;
        this.containerSupplier = containerSupplier;
    }

    public String getCreateTableQuery() {
        return createTableQuery;
    }

    public JdbcDatabaseContainer<?> newContainer() {
        return containerSupplier.get();
    }
}
