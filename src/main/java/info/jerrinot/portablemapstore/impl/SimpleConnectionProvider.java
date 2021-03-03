package info.jerrinot.portablemapstore.impl;

import com.hazelcast.core.HazelcastException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SimpleConnectionProvider implements ConnectionProvider {
    private final String url;
    private final String username;
    private final String password;

    public SimpleConnectionProvider(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new HazelcastException(e);
        }
    }
}
