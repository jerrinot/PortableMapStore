package info.jerrinot.portablemapstore.impl.columnmapping;

import java.sql.Connection;

public interface ConnectionProvider {
    Connection getConnection();
}
