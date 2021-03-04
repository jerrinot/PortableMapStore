package info.jerrinot.portablemapstore.impl.connectivity;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.function.FunctionEx;
import info.jerrinot.portablemapstore.impl.columnmapping.ConnectionProvider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbAccess {
    private final ConnectionProvider connectionProvider;

    public DbAccess(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public <T> T query(String sql, FunctionEx<ResultSet, T> resultSetMapper) {
        return query(sql, null, resultSetMapper);
    }

    public <T> T query(String sql, Object parameter, FunctionEx<ResultSet, T> resultSetMapper) {
        try (Connection conn = connectionProvider.getConnection()) {
            try (var stmt = conn.prepareStatement(sql)) {
                if (parameter != null) {
                    stmt.setObject(1, parameter);
                }
                try (var rs = stmt.executeQuery()) {
                    return resultSetMapper.apply(rs);
                }
            }
        } catch (SQLException e) {
            throw new HazelcastException(e);
        }
    }

    public <T> T query(String sql, FunctionEx<ResultSet, T> resultSetMapper, Object... params) {
        try (Connection conn = connectionProvider.getConnection()) {
            try (var stmt = conn.prepareStatement(sql)) {
                {
                    int i = 1;
                    for (Object key : params) {
                        stmt.setObject(i, key);
                        i++;
                    }
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    return resultSetMapper.apply(rs);
                }
            }
        } catch (SQLException e) {
            throw new HazelcastException(e);
        }
    }
}