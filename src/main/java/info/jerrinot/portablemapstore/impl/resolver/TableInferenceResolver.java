package info.jerrinot.portablemapstore.impl.resolver;

import com.hazelcast.function.FunctionEx;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import info.jerrinot.portablemapstore.impl.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class TableInferenceResolver implements ClassDefinitionResolver {
    private final JdbcTemplate jdbcTemplate;

    public TableInferenceResolver(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ClassDefinition resolve(int factoryId, int classId, String tableName) {
        String sql = "select * from " + tableName + " where 1 = 2";
        FunctionEx<ResultSet, ClassDefinition> mapper = (rs) -> {
            var cdBuilder = new ClassDefinitionBuilder(factoryId, classId);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i < columnCount + 1; i++) {
                String columnName = metaData.getColumnName(i);
                String columnClassName = metaData.getColumnClassName(i);
                var portableType = PortableType.getByClassname(columnClassName);
                switch (portableType) {
                    case UTF -> cdBuilder.addUTFField(columnName);
                    case INT -> cdBuilder.addIntField(columnName);
                }
            }
            return cdBuilder.build();
        };
        return jdbcTemplate.mapAll(mapper, sql, null);
    }

    private enum PortableType {
        UTF(String.class.getName()),
        INT(Integer.class.getName()),
        UNKNOWN("unknown");

        private final String classname;

        PortableType(String classname) {
            this.classname = classname;
        }

        public static PortableType getByClassname(String classnameToFind) {
            for (var type : PortableType.values()) {
                if (type.classname.equals(classnameToFind)) {
                    return type;
                }
            }
            return UNKNOWN;
        }

    }
}