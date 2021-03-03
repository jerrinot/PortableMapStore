package info.jerrinot.portablemapstore;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.GenericRecord;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockedHazelcastPortableMapLoaderTest extends BasePortableMapLoaderTest {

    @Test
    public void load_smoke() {
        PortableMapLoader mapLoader = createPortableLoaderInstance();

        GenericRecord record = mapLoader.load(0);
        assertEquals(0, record.readInt("id"));
        assertEquals("name", record.readUTF("name"));
        assertEquals("lastname", record.readUTF("lastname"));
    }

    @Test
    public void loadAll_smoke() {
        PortableMapLoader mapLoader = createPortableLoaderInstance();

        Map<Object, GenericRecord> loadedEntries = mapLoader.loadAll(Arrays.asList(0, 1, 2, 3, 4, 5));
        assertEquals(1, loadedEntries.size());
        GenericRecord record = loadedEntries.get(0);

        assertEquals(0, record.readInt("id"));
        assertEquals("name", record.readUTF("name"));
        assertEquals("lastname", record.readUTF("lastname"));
    }

    @Test
    public void loadAllKeys_smoke() {
        PortableMapLoader mapLoader = createPortableLoaderInstance();

        Iterable<Object> keys = mapLoader.loadAllKeys();
        Iterator<Object> keyIter = keys.iterator();
        assertTrue(keyIter.hasNext());
        Object key = keyIter.next();
        assertEquals(0, key);
    }

    @NotNull
    private PortableMapLoader createPortableLoaderInstance() {
        Properties props = createProps();
        var mapLoader = new PortableMapLoader();
        HazelcastInstance instance = mock(HazelcastInstance.class);
        Config config = new Config();
        ClassDefinition cd = createClassDefinition();
        config.getSerializationConfig().addClassDefinition(cd);
        when(instance.getConfig()).thenReturn(config);
        mapLoader.init(instance, props, "map");
        return mapLoader;
    }
}