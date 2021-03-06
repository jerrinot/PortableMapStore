package info.jerrinot.portablemapstore.impl.connectivity;

import com.hazelcast.config.InvalidConfigurationException;
import org.junit.Test;

import static org.junit.Assert.*;

public class PoolTypeTest {

    @Test
    public void testByKey_null() {
        var poolType = PoolType.getByKey(null);
        assertEquals(PoolType.SIMPLE, poolType);
    }

    @Test
    public void testByKey_empty() {
        var poolType = PoolType.getByKey("");
        assertEquals(PoolType.SIMPLE, poolType);
    }

    @Test
    public void testByKey_hikari() {
        var poolType = PoolType.getByKey("hikari");
        assertEquals(PoolType.HIKARI, poolType);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testByKey_unknown() {
        var poolType = PoolType.getByKey("rubbish");
    }

}