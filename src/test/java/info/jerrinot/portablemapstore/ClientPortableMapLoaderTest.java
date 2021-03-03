package info.jerrinot.portablemapstore;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.nio.serialization.GenericRecord;
import info.jerrinot.portablemapstore.test.domain.Person;
import info.jerrinot.portablemapstore.test.domain.PersonFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientPortableMapLoaderTest extends BasePortableMapLoaderTest {
    private HazelcastInstance server;

    @Before
    public void setup() {
        var config = new Config();
        config.getSerializationConfig().addClassDefinition(createClassDefinition());
        server = Hazelcast.newHazelcastInstance(config);
    }

    @After
    public void tearDown() {
        server.shutdown();
    }

    @Test
    public void getWhenPortableNotRegisteredOnClient() {
        var client = HazelcastClient.newHazelcastClient();
        configureMap(client);

        IMap<Integer, GenericRecord> map = client.getMap("map");
        GenericRecord record = map.get(0);
        assertEquals(0, record.readInt("id"));
        assertEquals("name", record.readUTF("name"));
        assertEquals("lastname", record.readUTF("lastname"));
    }

    @Test
    public void getWhenPortableIsRegisteredOnClient() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getSerializationConfig().addPortableFactoryClass(1, PersonFactory.class.getName());
        var client = HazelcastClient.newHazelcastClient(clientConfig);
        configureMap(client);

        IMap<Integer, Person> map = client.getMap("map");
        var person = map.get(0);
        assertEquals(0, person.getId());
        assertEquals("name", person.getName());
        assertEquals("lastname", person.getLastname());
    }

    private void configureMap(HazelcastInstance client) {
        MapConfig mapConfig = new MapConfig("map");
        mapConfig.getMapStoreConfig()
                .setClassName(PortableMapLoader.class.getName())
                .setProperties(createProps())
                .setEnabled(true);
        client.getConfig().addMapConfig(mapConfig);
    }
}
