package info.jerrinot.portablemapstore.impl.dbmapper;

import com.hazelcast.function.FunctionEx;

import java.sql.ResultSet;

public final class ResultSetToSingleObjectOrNull<T> implements FunctionEx<ResultSet, T> {
    private final FunctionEx<ResultSet, T> rowToObject;

    public ResultSetToSingleObjectOrNull(FunctionEx<ResultSet, T> rowToObject) {
        this.rowToObject = rowToObject;
    }

    @Override
    public T applyEx(ResultSet resultSet) throws Exception {
        if (!resultSet.next()) {
            return null;
        }
        var res = rowToObject.apply(resultSet);
        if (resultSet.next()) {
            throw new IllegalStateException("result set unexpectedly returned more than one row");
        }
        return res;
    }
}
