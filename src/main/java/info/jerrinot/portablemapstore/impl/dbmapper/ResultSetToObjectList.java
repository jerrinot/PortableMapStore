package info.jerrinot.portablemapstore.impl.dbmapper;

import com.hazelcast.function.FunctionEx;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ResultSetToObjectList<T> implements FunctionEx<ResultSet, List<T>> {
    private final FunctionEx<ResultSet, T> rowToObject;

    public ResultSetToObjectList(FunctionEx<ResultSet, T> rowToObject) {
        this.rowToObject = rowToObject;
    }

    @Override
    public List<T> applyEx(ResultSet resultSet) throws Exception {
        var result = new ArrayList<T>();
        while (resultSet.next()) {
            result.add(rowToObject.apply(resultSet));
        }
        return result;
    }
}
