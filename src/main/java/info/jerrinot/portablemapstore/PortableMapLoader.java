package info.jerrinot.portablemapstore;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.function.FunctionEx;
import com.hazelcast.map.MapLoader;
import com.hazelcast.map.MapLoaderLifecycleSupport;
import com.hazelcast.nio.serialization.GenericRecord;
import info.jerrinot.portablemapstore.impl.columnmapping.ColumnFieldMappings;
import info.jerrinot.portablemapstore.impl.columnmapping.MappingParser;
import info.jerrinot.portablemapstore.impl.connectivity.ConnectionProvider;
import info.jerrinot.portablemapstore.impl.connectivity.ConnectionProviderFactory;
import info.jerrinot.portablemapstore.impl.connectivity.DbAccess;
import info.jerrinot.portablemapstore.impl.dbmapper.ResultSetToEntryMap;
import info.jerrinot.portablemapstore.impl.dbmapper.ResultSetToObjectList;
import info.jerrinot.portablemapstore.impl.dbmapper.ResultSetToSingleObjectOrNull;
import info.jerrinot.portablemapstore.impl.dbmapper.RowToPortable;
import info.jerrinot.portablemapstore.impl.resolver.ChainingClassDefinitionResolver;
import info.jerrinot.portablemapstore.impl.resolver.StaticClassDefinitionResolver;
import info.jerrinot.portablemapstore.impl.resolver.TableInferenceResolver;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class PortableMapLoader implements MapLoader<Object, GenericRecord>, MapLoaderLifecycleSupport {
    private DbAccess dbAccess;
    private FunctionEx<ResultSet, GenericRecord> resultSetToPortable;
    private FunctionEx<ResultSet, Map<Object, GenericRecord>> resultSetToEntries;
    private FunctionEx<ResultSet, List<Object>> resultSetToKeys;
    private String findByKeyQuery;
    private String findByKeysQuery;
    private String findAllKeysQuery;

    @Override
    public GenericRecord load(Object key) {
        return dbAccess.query(findByKeyQuery, key, resultSetToPortable);
    }

    @Override
    public Map<Object, GenericRecord> loadAll(Collection keys) {
        //todo: limit max size of the collection, do chunking?
        String sql = String.format(findByKeysQuery, placeHolders(keys.size()));
        // this replace the (%s) in the original query with (?, ?, ?, ? ... ?) for each key
        return dbAccess.query(sql, resultSetToEntries, keys.toArray());
    }

    @Override
    public Iterable<Object> loadAllKeys() {
        return dbAccess.query(findAllKeysQuery, resultSetToKeys);
    }

    @Override
    public void init(HazelcastInstance hazelcastInstance, Properties properties, String mapName) {
        var keyColumnName = properties.getProperty("keyColumnName", "id");
        var tableName = properties.getProperty("tableName", mapName);
        findByKeyQuery  = "select * from " + tableName + " where " + keyColumnName + " = ?";
        findByKeysQuery = "select * from " + tableName + " where " + keyColumnName + " IN (%s)";
        findAllKeysQuery = "select " + keyColumnName + " from " + tableName;

        ConnectionProvider connectionProvider = ConnectionProviderFactory.newProvider(properties);
        dbAccess = new DbAccess(connectionProvider);

        var factoryId = Integer.parseInt(properties.getProperty("factoryId"));
        var classId = Integer.parseInt(properties.getProperty("classId"));
        var definitions = hazelcastInstance.getConfig().getSerializationConfig().getClassDefinitions();
        var staticResolver = new StaticClassDefinitionResolver(definitions);
        ColumnFieldMappings mappings = MappingParser.createMappings(properties);
        var inference = new TableInferenceResolver(dbAccess, mappings);
        var cdSelector = new ChainingClassDefinitionResolver(staticResolver, inference);
        var cd = cdSelector.resolve(factoryId, classId, tableName);
        var rowToPortable = new RowToPortable(cd, mappings);
        resultSetToPortable = new ResultSetToSingleObjectOrNull<>(rowToPortable);
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
