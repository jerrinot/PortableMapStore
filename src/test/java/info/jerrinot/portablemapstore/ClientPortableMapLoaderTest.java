package info.jerrinot.portablemapstore;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.nio.serialization.GenericRecord;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import info.jerrinot.portablemapstore.test.domain.Person;
import info.jerrinot.portablemapstore.test.domain.PersonFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class ClientPortableMapLoaderTest extends BasePortableMapLoaderTest {
    private HazelcastInstance server;

    @Before
    public void setup() {
        server = Hazelcast.newHazelcastInstance();
    }

    @After
    public void tearDown() {
        if (server != null) {
            server.shutdown();
        }
    }

    @Test
    public void getWhenPortableNotRegisteredOnClient() {
        var client = HazelcastClient.newHazelcastClient();
        configureMap(client);

        IMap<Integer, GenericRecord> map = client.getMap("map");
        for (int i = 0; i < ROW_COUNT; i++) {
            GenericRecord record = map.get(i);
            assertEquals(i, record.readInt("id"));
            assertEquals("name" + i, record.readUTF("name"));
            assertEquals("lastname" + i, record.readUTF("lastname"));
            assertEquals(i, record.readDouble("doubleField"), 0.1);
            assertEquals(i % 2 == 0, record.readBoolean("boolean"));
        }
        assertNull(map.get(ROW_COUNT + 1));
    }

    @Test
    public void getWhenPortableIsRegisteredOnClient() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getSerializationConfig().addPortableFactoryClass(1, PersonFactory.class.getName());
        var client = HazelcastClient.newHazelcastClient(clientConfig);
        configureMap(client);

        IMap<Integer, Person> map = client.getMap("map");
        for (int i = 0; i < ROW_COUNT; i++) {
            var person = map.get(i);
            assertEquals(i, person.getId());
            assertEquals("name" + i, person.getName());
            assertEquals("lastname" + i, person.getLastname());
            assertEquals(i, person.getDoubleField(), 0.1);
            assertEquals(i % 2 == 0, person.isBooleanField());
        }
        assertNull(map.get(ROW_COUNT + 1));
    }

    @Test
    public void testIndexedPredicateQuery() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getSerializationConfig().addPortableFactoryClass(1, PersonFactory.class.getName());
        var client = HazelcastClient.newHazelcastClient(clientConfig);
        configureMap(client);

        IMap<Integer, Person> map = client.getMap("map");
        Predicate<Integer, Person> predicate = Predicates.equal("lastname", "lastname1");
        Collection<Person> values = map.values(predicate);
        assertEquals(1, values.size());
        Person person = values.iterator().next();
        assertEquals(1, person.getId());
        assertEquals("name1", person.getName());
        assertEquals("lastname1", person.getLastname());
        assertEquals(1, person.getDoubleField(), 0.1);
        assertFalse(person.isBooleanField());
    }

    private void configureMap(HazelcastInstance client) {
        MapConfig mapConfig = new MapConfig("map");
        mapConfig.getMapStoreConfig()
                .setClassName(PortableMapLoader.class.getName())
                .setProperties(createProps())
                .setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER)
                .setEnabled(true);
        mapConfig.addIndexConfig(new IndexConfig(IndexConfig.DEFAULT_TYPE, "lastname"));

        client.getConfig().addMapConfig(mapConfig);
    }
}
