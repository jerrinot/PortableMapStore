package info.jerrinot.portablemapstore.impl.connectivity;

import java.util.Properties;

public final class ConnectionProviderFactory {
    private ConnectionProviderFactory() {

    }

    public static final ConnectionProvider newProvider(Properties properties) {
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        var providerType = ConnectionType.getByKey(properties.getProperty("poolType"));
        return switch (providerType) {
            case SIMPLE -> new SimpleConnectionProvider(url, username, password);
            case HIKARI -> new HikariConnectionProvider(url, username, password);
        };
    }
}
