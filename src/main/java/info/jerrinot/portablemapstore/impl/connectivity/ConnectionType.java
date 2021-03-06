package info.jerrinot.portablemapstore.impl.connectivity;

import com.hazelcast.config.InvalidConfigurationException;

public enum ConnectionType {
    SIMPLE("simple"),
    HIKARI("hikari");

    private static final ConnectionType DEFAULT = SIMPLE;

    private final String key;

    ConnectionType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static ConnectionType getByKey(String key) {
        if (key == null || key.isBlank()) {
            return DEFAULT;
        }
        for (var type : ConnectionType.values()) {
            if (type.getKey().equals(key)) {
                return type;
            }
        }
        throw new InvalidConfigurationException("unknown connection provider: " + key);
    }
}
