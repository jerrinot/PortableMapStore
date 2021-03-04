package info.jerrinot.portablemapstore.impl.dbmapper;

import com.hazelcast.function.FunctionEx;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ResultSetToEntryMap<K, V> implements FunctionEx<ResultSet, Map<K, V>> {
    private final FunctionEx<ResultSet, K> rowToKey;
    private final FunctionEx<ResultSet, V> rowToValue;

    public ResultSetToEntryMap(FunctionEx<ResultSet, K> rowToKey, FunctionEx<ResultSet, V> rowToValue) {
        this.rowToKey = rowToKey;
        this.rowToValue = rowToValue;
    }


    @Override
    public Map<K, V> applyEx(ResultSet resultSet) throws Exception {
        var result = new HashMap<K, V>();
        while (resultSet.next()) {
            K key = rowToKey.apply(resultSet);
            V value = rowToValue.applyEx(resultSet);
            result.put(key, value);
        }
        return result;
    }
}
