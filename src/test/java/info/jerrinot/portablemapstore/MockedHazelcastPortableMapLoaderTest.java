package info.jerrinot.portablemapstore;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.GenericRecord;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockedHazelcastPortableMapLoaderTest extends BasePortableMapLoaderTest {

    @Test
    public void load_smoke() {
        PortableMapLoader mapLoader = createPortableLoaderInstance();
        for (int i = 0; i < ROW_COUNT; i++) {
            GenericRecord record = mapLoader.load(i);
            assertEquals(i, record.readInt("id"));
            assertEquals("name" + i, record.readUTF("name"));
            assertEquals("lastname" + i, record.readUTF("lastname"));
            assertEquals(i, record.readDouble("doubleField"), 0.1);
            assertEquals(i % 2 == 0, record.readBoolean("boolean"));
        }
        assertNull(mapLoader.load(ROW_COUNT + 1));
    }

    @Test
    public void loadAll_smoke() {
        PortableMapLoader mapLoader = createPortableLoaderInstance();

        Map<Object, GenericRecord> loadedEntries = mapLoader.loadAll(Arrays.asList(-2, -1, 0, 1, 2, 3, 4, 5));
        assertEquals(6, loadedEntries.size());
        for (int i = 0; i < 6; i++) {
            var record = loadedEntries.get(i);
            assertEquals(i, record.readInt("id"));
            assertEquals("name" + i, record.readUTF("name"));
            assertEquals("lastname" + i, record.readUTF("lastname"));
            assertEquals(i, record.readDouble("doubleField"), 0.1);
            assertEquals(i % 2 == 0, record.readBoolean("boolean"));
        }
    }

    @Test
    public void loadAllKeys_smoke() {
        PortableMapLoader mapLoader = createPortableLoaderInstance();

        Iterable<Object> keys = mapLoader.loadAllKeys();
        Iterator<Object> keyIter = keys.iterator();
        for (int i = 0; i < ROW_COUNT; i++) {
            assertTrue(keyIter.hasNext());
            Object key = keyIter.next();
            assertEquals(i, key);
        }
        assertFalse(keyIter.hasNext());
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

    @NotNull
    public static ClassDefinition createClassDefinition() {
        return new ClassDefinitionBuilder(1, 1)
                .addIntField("id")
                .addUTFField("name")
                .addUTFField("lastname")
                .addDoubleField("doubleField")
                .addBooleanField("boolean")
                .build();
    }
}