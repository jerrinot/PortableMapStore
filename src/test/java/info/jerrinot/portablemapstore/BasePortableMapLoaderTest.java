package info.jerrinot.portablemapstore;

import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.util.Properties;

public class BasePortableMapLoaderTest {
    private static final String CREATE_TABLE = """
            CREATE TABLE map (
                id          integer PRIMARY KEY,
                name        varchar(40) NOT NULL,
                lastname    varchar(40) NOT NULL
            )""";

    private static final String INSERT_PERSON = "insert into map values (0, 'name', 'lastname');";

    @ClassRule
    public static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa");

    @BeforeClass
    public static void prepareData() throws Exception {
        Connection connection = postgres.createConnection("");
        connection.prepareStatement(CREATE_TABLE).execute();
        connection.prepareStatement(INSERT_PERSON).execute();
    }

    public static Properties createProps() {
        var props = new Properties();
        props.setProperty("url", postgres.getJdbcUrl());
        props.setProperty("username", postgres.getUsername());
        props.setProperty("password", postgres.getPassword());
        props.setProperty("factoryId", "1");
        props.setProperty("classId", "1");
        return props;
    }

    @NotNull
    public static ClassDefinition createClassDefinition() {
        return new ClassDefinitionBuilder(1, 1)
                .addIntField("id")
                .addUTFField("name")
                .addUTFField("lastname")
                .build();
    }
}
