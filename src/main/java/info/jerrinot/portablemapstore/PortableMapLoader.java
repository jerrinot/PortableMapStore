package info.jerrinot.portablemapstore;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.function.FunctionEx;
import com.hazelcast.map.MapLoader;
import com.hazelcast.map.MapLoaderLifecycleSupport;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.GenericRecord;
import info.jerrinot.portablemapstore.impl.ClassDefinitionResolver;
import info.jerrinot.portablemapstore.impl.SimpleConnectionProvider;
import info.jerrinot.portablemapstore.impl.JdbcTemplate;
import info.jerrinot.portablemapstore.impl.mapper.ResultSetToEntryMap;
import info.jerrinot.portablemapstore.impl.mapper.ResultSetToObjectList;
import info.jerrinot.portablemapstore.impl.mapper.ResultSetToPortable;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public final class PortableMapLoader implements MapLoader<Object, GenericRecord>, MapLoaderLifecycleSupport {
    private JdbcTemplate jdbcTemplate;
    private FunctionEx<ResultSet, GenericRecord> rowToPortable;
    private FunctionEx<ResultSet, Map<Object, GenericRecord>> resultSetToEntries;
    private FunctionEx<ResultSet, List<Object>> resultSetToKeys;
    private String findByKeyQuery;
    private String findByKeysQuery;
    private String findAllKeysQuery;

    @Override
    public GenericRecord load(Object key) {
        return jdbcTemplate.findOneOrNull(rowToPortable, findByKeyQuery, key);
    }

    @Override
    public Map<Object, GenericRecord> loadAll(Collection keys) {
        //todo: limit max size of the collection, do chunking?
        String sql = String.format(findByKeysQuery, placeHolders(keys.size()));
        return jdbcTemplate.query(resultSetToEntries, sql, keys);
    }

    @Override
    public Iterable<Object> loadAllKeys() {
        return jdbcTemplate.query(resultSetToKeys, findAllKeysQuery, Collections.EMPTY_LIST);
    }

    @Override
    public void init(HazelcastInstance hazelcastInstance, Properties properties, String mapName) {
        var keyColumnName = properties.getProperty("keyColumnName", "id");
        var tableName = properties.getProperty("tableName", mapName);
        findByKeyQuery  = "select * from " + tableName + " where " + keyColumnName + " = ?";
        findByKeysQuery = "select * from " + tableName + " where " + keyColumnName + " IN (%s)";
        findAllKeysQuery = "select " + keyColumnName + " from " + tableName;

        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        var connectionProvider = new SimpleConnectionProvider(url, username, password);
        jdbcTemplate = new JdbcTemplate(connectionProvider);

        var factoryId = Integer.parseInt(properties.getProperty("factoryId"));
        var classId = Integer.parseInt(properties.getProperty("classId"));
        var definitions = hazelcastInstance.getConfig().getSerializationConfig().getClassDefinitions();
        var cdSelector = new ClassDefinitionResolver(definitions, jdbcTemplate);
        rowToPortable = new ResultSetToPortable(cdSelector.resolve(factoryId, classId, tableName));
        resultSetToEntries = new ResultSetToEntryMap<>(resultSet -> resultSet.getObject(keyColumnName), rowToPortable);
        resultSetToKeys = new ResultSetToObjectList<>(resultSet -> resultSet.getObject(keyColumnName));
    }

    private static String placeHolders(int length) {
        return String.join(",", Collections.nCopies(length, "?"));
    }

    @Override
    public void destroy() {

    }
}
