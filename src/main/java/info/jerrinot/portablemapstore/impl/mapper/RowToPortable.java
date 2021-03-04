package info.jerrinot.portablemapstore.impl.mapper;

import com.hazelcast.function.FunctionEx;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.nio.serialization.GenericRecord;

import java.sql.ResultSet;

public final class RowToPortable implements FunctionEx<ResultSet, GenericRecord> {
    private final ClassDefinition classDefinition;

    public RowToPortable(ClassDefinition classDefinition) {
        this.classDefinition = classDefinition;
    }

    @Override
    public GenericRecord applyEx(ResultSet rs) throws Exception {
        GenericRecord.Builder portable = GenericRecord.Builder.portable(classDefinition);
        for (int i = 0; i < classDefinition.getFieldCount(); i++) {
            FieldDefinition field = classDefinition.getField(i);
            String fieldName = field.getName();
            FieldType type = field.getType();
            switch (type) {
                case PORTABLE, LONG, BYTE, BOOLEAN, CHAR, SHORT, FLOAT, DOUBLE, PORTABLE_ARRAY, BYTE_ARRAY, BOOLEAN_ARRAY, CHAR_ARRAY, SHORT_ARRAY, INT_ARRAY, LONG_ARRAY, FLOAT_ARRAY, DOUBLE_ARRAY, UTF_ARRAY -> {
                    throw new UnsupportedOperationException("not implemented yet");
                }
                case INT -> portable.writeInt(fieldName, rs.getInt(fieldName));
                case UTF -> portable.writeUTF(fieldName, rs.getString(fieldName));
            }
        }
        return portable.build();
    }
}
