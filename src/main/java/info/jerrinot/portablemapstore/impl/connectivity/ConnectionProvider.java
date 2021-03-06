package info.jerrinot.portablemapstore.impl.connectivity;

import java.sql.Connection;

public interface ConnectionProvider {
    Connection getConnection();
}
