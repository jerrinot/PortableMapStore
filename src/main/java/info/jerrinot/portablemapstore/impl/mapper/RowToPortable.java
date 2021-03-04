package info.jerrinot.portablemapstore.impl.mapper;

import com.hazelcast.function.FunctionEx;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.nio.serialization.GenericRecord;
import info.jerrinot.portablemapstore.impl.columnmapping.ColumnFieldMappings;

import java.sql.ResultSet;

public final class RowToPortable implements FunctionEx<ResultSet, GenericRecord> {
    private final ClassDefinition classDefinition;
    private final ColumnFieldMappings mappings;

    public RowToPortable(ClassDefinition classDefinition, ColumnFieldMappings mappings) {
        this.classDefinition = classDefinition;
        this.mappings = mappings;
    }

    @Override
    public GenericRecord applyEx(ResultSet rs) throws Exception {
        GenericRecord.Builder portable = GenericRecord.Builder.portable(classDefinition);
        for (int i = 0; i < classDefinition.getFieldCount(); i++) {
            FieldDefinition field = classDefinition.getField(i);
            String fieldName = field.getName();
            var columnName = mappings.fieldToColumn(fieldName);
            if (columnName != null) {
                FieldType type = field.getType();
                switch (type) {
                    case PORTABLE, CHAR, PORTABLE_ARRAY, BYTE_ARRAY,
                            BOOLEAN_ARRAY, CHAR_ARRAY, SHORT_ARRAY, INT_ARRAY, LONG_ARRAY, FLOAT_ARRAY,
                            DOUBLE_ARRAY, UTF_ARRAY -> {
                        throw new UnsupportedOperationException(type + "not implemented yet");
                    }
                    case INT -> portable.writeInt(fieldName, rs.getInt(columnName));
                    case UTF -> portable.writeUTF(fieldName, rs.getString(columnName));
                    case LONG -> portable.writeLong(fieldName, rs.getLong(columnName));
                    case BYTE -> portable.writeLong(fieldName, rs.getByte(columnName));
                    case BOOLEAN -> portable.writeBoolean(fieldName, rs.getBoolean(columnName));
                    case SHORT -> portable.writeShort(fieldName, rs.getShort(columnName));
                    case DOUBLE -> portable.writeDouble(fieldName, rs.getDouble(columnName));
                    case FLOAT -> portable.writeFloat(fieldName, rs.getFloat(columnName));
                }
            }
        }
        return portable.build();
    }
}
