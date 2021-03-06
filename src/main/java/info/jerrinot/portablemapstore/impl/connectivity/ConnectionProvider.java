package info.jerrinot.portablemapstore.impl.connectivity;

import java.sql.Connection;

public interface ConnectionProvider extends AutoCloseable {
    Connection getConnection();

    @Override
    default void close() {

    }
}
