package info.jerrinot.portablemapstore.impl;

import java.sql.Connection;

public interface ConnectionProvider {
    Connection getConnection();
}
