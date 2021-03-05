package info.jerrinot.portablemapstore;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Properties;

public class BasePortableMapLoaderTest {
    public static final int ROW_COUNT = 100;

    private static final String CREATE_TABLE = """
            CREATE TABLE map (
                id          integer PRIMARY KEY,
                name        varchar(40) NOT NULL,
                lastname    varchar(40) NOT NULL,
                double      double precision,
                boolean     boolean
            )""";

    private static final String INSERT_PERSON = "insert into map values (?, ?, ?, ?, ?);";

    @ClassRule
    public static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa");

    @BeforeClass
    public static void prepareData() throws Exception {
        try (var connection = postgres.createConnection("")) {
            connection.prepareStatement(CREATE_TABLE).execute();
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
    }

    public static Properties createProps() {
        var props = new Properties();
        props.setProperty("url", postgres.getJdbcUrl());
        props.setProperty("username", postgres.getUsername());
        props.setProperty("password", postgres.getPassword());
        props.setProperty("factoryId", "1");
        props.setProperty("classId", "1");
        props.setProperty("COLUMN:double=FIELD:doubleField", "");
        return props;
    }
}
