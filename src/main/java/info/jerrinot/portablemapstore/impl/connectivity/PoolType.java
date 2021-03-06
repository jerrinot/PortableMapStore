package info.jerrinot.portablemapstore.impl.connectivity;

import com.hazelcast.config.InvalidConfigurationException;

public enum PoolType {
    SIMPLE("simple"),
    HIKARI("hikari");

    private static final PoolType DEFAULT = SIMPLE;

    private final String key;

    PoolType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static PoolType getByKey(String key) {
        if (key == null || key.isBlank()) {
            return DEFAULT;
        }
        for (var type : PoolType.values()) {
            if (type.getKey().equals(key)) {
                return type;
            }
        }
        throw new InvalidConfigurationException("unknown connection provider: " + key);
    }
}
