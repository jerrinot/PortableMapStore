package info.jerrinot.portablemapstore.impl.connectivity;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class ConnectionProviderFactoryTest {
    @Test
    public void testDefaultIsSimpleConnectionProvider() {
        var props = new Properties();
        var connectionProvider = ConnectionProviderFactory.newProvider(props);
        assertTrue(connectionProvider instanceof SimpleConnectionProvider);
    }

    @Test
    public void testDefaultHikari() {
        var props = new Properties();
        props.setProperty("url", "jdbc:h2:mem:");
        props.setProperty("poolType", "hikari");
        var connectionProvider = ConnectionProviderFactory.newProvider(props);
        assertTrue(connectionProvider instanceof HikariConnectionProvider);
    }

    @Test
    public void testExplicitSimple() {
        var props = new Properties();
        props.setProperty("poolType", "simple");
        var connectionProvider = ConnectionProviderFactory.newProvider(props);
        assertTrue(connectionProvider instanceof SimpleConnectionProvider);
    }

}