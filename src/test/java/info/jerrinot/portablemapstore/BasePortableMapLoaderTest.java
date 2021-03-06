package info.jerrinot.portablemapstore;

import info.jerrinot.portablemapstore.impl.connectivity.PoolType;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

@RunWith(Parameterized.class)
public abstract class BasePortableMapLoaderTest {
    public static final int ROW_COUNT = 100;
    private static final String INSERT_PERSON = "insert into map values (?, ?, ?, ?, ?);";

    @Parameterized.Parameter(0)
    public static SupportedDatabases database;

    @Parameterized.Parameter(1)
    public static PoolType poolType;

    private JdbcDatabaseContainer<?> container;

    @Parameterized.Parameters(name = "database:{0}, connectionType:{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {SupportedDatabases.POSTGRES, PoolType.HIKARI},
                {SupportedDatabases.POSTGRES, PoolType.SIMPLE},
                {SupportedDatabases.MARIADB, PoolType.HIKARI},
                {SupportedDatabases.MARIADB, PoolType.SIMPLE},
                {SupportedDatabases.MSSQL, PoolType.HIKARI},
                {SupportedDatabases.MSSQL, PoolType.SIMPLE},
                {SupportedDatabases.DB2, PoolType.HIKARI},
                {SupportedDatabases.DB2, PoolType.SIMPLE},
        });
    }

    @Before
    public void startContainerAndPrepareData() throws Exception {
        container = database.newContainer();
        container.start();
        try (var connection = container.createConnection("")) {
            prepareData(connection);
        }
    }

    private static void prepareData(Connection connection) throws SQLException {
        connection.prepareStatement(database.getCreateTableQuery()).execute();
        var stmt = connection.prepareStatement(INSERT_PERSON);
        for (int i = 0; i < ROW_COUNT; i++) {
            stmt.setInt(1, i);
            stmt.setString(2, "name" + i);
            stmt.setString(3, "lastname" + i);
            stmt.setDouble(4, i);
            stmt.setBoolean(5, i % 2 == 0);
            stmt.executeUpdate();
        }
    }

    @After
    public void stopContainer() {
        if (container != null) {
            container.stop();
        }
    }

    public Properties createProps() {
        var props = new Properties();
        props.setProperty("url", container.getJdbcUrl());
        props.setProperty("username", container.getUsername());
        props.setProperty("password", container.getPassword());
        props.setProperty("factoryId", "1");
        props.setProperty("classId", "1");
        if (SupportedDatabases.DB2 == database) {
            props.setProperty("COLUMN:ID=FIELD:id", "");
            props.setProperty("COLUMN:NAME=FIELD:name", "");
            props.setProperty("COLUMN:LASTNAME=FIELD:lastname", "");
            props.setProperty("COLUMN:DOUBLECOLUMN=FIELD:doubleField", "");
            props.setProperty("COLUMN:BOOLEANCOLUMN=FIELD:boolean", "");
        } else {
            props.setProperty("COLUMN:doublecolumn=FIELD:doubleField", "");
            props.setProperty("COLUMN:booleancolumn=FIELD:boolean", "");
        }
        props.setProperty("poolType", poolType.getKey());
        return props;
    }
}
