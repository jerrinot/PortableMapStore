package info.jerrinot.portablemapstore.impl;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.function.FunctionEx;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Optional;
import java.util.Set;

public final class ClassDefinitionResolver {
    private final Set<ClassDefinition> staticDefinitions;
    private final JdbcTemplate jdbcTemplate;

    public ClassDefinitionResolver(Set<ClassDefinition> staticDefinitions, JdbcTemplate jdbcTemplate) {
        this.staticDefinitions = staticDefinitions;
        this.jdbcTemplate = jdbcTemplate;
    }

    public ClassDefinition resolve(int factoryId, int classId, String tableName) {
        return staticDefinitions
                .stream()
                .filter(cd -> cd.getFactoryId() == factoryId && cd.getClassId() == classId)
                .findFirst()
                .or(() -> {
            String sql = "select * from " + tableName + " where 1 = 2";
            FunctionEx<ResultSet, ClassDefinition> mapper = (rs) -> {
                var cdBuilder = new ClassDefinitionBuilder(factoryId, classId);
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i < columnCount + 1; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnClassName = metaData.getColumnClassName(i);
                    switch (columnClassName) {
                        case "java.lang.String" -> cdBuilder.addUTFField(columnName);
                        case "java.lang.Integer" -> cdBuilder.addIntField(columnName);
                        default -> throw new UnsupportedOperationException(columnClassName + " not supported");
                    }
                }
                return cdBuilder.build();
            };
            return Optional.of(jdbcTemplate.mapAll(mapper, sql, null));
        }).orElseThrow(() -> new HazelcastException("class definition not found"));
    }
}
