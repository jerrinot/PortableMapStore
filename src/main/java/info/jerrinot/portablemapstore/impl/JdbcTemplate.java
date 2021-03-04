package info.jerrinot.portablemapstore.impl;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.function.FunctionEx;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public final class JdbcTemplate {
    private final ConnectionProvider connectionProvider;

    public JdbcTemplate(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public <T> T findOneOrNull(FunctionEx<ResultSet, T> rowMapper, String sql, Object parameter) {
        try (Connection conn = connectionProvider.getConnection()) {
            try (var stmt = conn.prepareStatement(sql)) {
                if (parameter != null) {
                    stmt.setObject(1, parameter);
                }
                try (var rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    return rowMapper.apply(rs);
                }
            }
        } catch (SQLException e) {
            throw new HazelcastException(e);
        }
    }

    public <T> T mapAll(FunctionEx<ResultSet, T> resultSetMapper, String sql, Object parameter) {
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

    public <T> T query(FunctionEx<ResultSet, T> resultSetMapper, String sql, Collection params) {
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