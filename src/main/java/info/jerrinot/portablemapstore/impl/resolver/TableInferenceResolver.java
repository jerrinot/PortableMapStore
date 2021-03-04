package info.jerrinot.portablemapstore.impl.resolver;

import com.hazelcast.function.FunctionEx;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import info.jerrinot.portablemapstore.impl.ColumnFieldMappings;
import info.jerrinot.portablemapstore.impl.DbAccess;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class TableInferenceResolver implements ClassDefinitionResolver {
    private final DbAccess dbAccess;
    private final ColumnFieldMappings mappings;

    public TableInferenceResolver(DbAccess dbAccess, ColumnFieldMappings mappings) {
        this.dbAccess = dbAccess;
        this.mappings = mappings;
    }

    @Override
    public ClassDefinition resolve(int factoryId, int classId, String tableName) {
        String sql = "select * from " + tableName + " where 1 = 2";
        FunctionEx<ResultSet, ClassDefinition> mapper = (rs) -> {
            var cdBuilder = new ClassDefinitionBuilder(factoryId, classId);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i < columnCount + 1; i++) {
                addColumn(cdBuilder, metaData, i);
            }
            return cdBuilder.build();
        };
        return dbAccess.query(sql, mapper);
    }

    private void addColumn(ClassDefinitionBuilder builder, ResultSetMetaData metaData, int index) throws SQLException {
        String columnName = metaData.getColumnName(index);
        var fieldName = mappings.columnToField(columnName);
        if (fieldName != null) {
            String columnClassName = metaData.getColumnClassName(index);
            var portableType = PortableType.getByClassname(columnClassName);
            switch (portableType) {
                case UTF -> builder.addUTFField(fieldName);
                case INT -> builder.addIntField(fieldName);
                case LONG -> builder.addLongArrayField(fieldName);
                case BYTE -> builder.addByteField(fieldName);
                case BOOLEAN -> builder.addBooleanField(fieldName);
                case SHORT -> builder.addShortField(fieldName);
                case DOUBLE -> builder.addDoubleField(fieldName);
                case FLOAT -> builder.addFloatField(fieldName);
                case UNKNOWN -> {
                    // todo: print warning
                }
            }
        }
    }

    private enum PortableType {
        UTF(String.class.getName()),
        INT(Integer.class.getName()),
        LONG(Long.class.getName()),
        BYTE(Byte.class.getName()),
        BOOLEAN(Boolean.class.getName()),
        SHORT(Short.class.getName()),
        DOUBLE(Double.class.getName()),
        FLOAT(Float.class.getName()),
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