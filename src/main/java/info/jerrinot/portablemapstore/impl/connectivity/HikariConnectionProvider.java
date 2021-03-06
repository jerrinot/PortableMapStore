package info.jerrinot.portablemapstore.impl.connectivity;

import com.hazelcast.core.HazelcastException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariConnectionProvider implements ConnectionProvider {
    private final HikariDataSource ds;

    public HikariConnectionProvider(String url, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        this.ds = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new HazelcastException(e);
        }
    }

    @Override
    public void close() {
        ds.close();
    }
}
