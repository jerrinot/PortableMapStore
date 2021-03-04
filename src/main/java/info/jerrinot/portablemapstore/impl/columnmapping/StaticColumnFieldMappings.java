package info.jerrinot.portablemapstore.impl.columnmapping;

import java.util.HashMap;
import java.util.Map;

public final class StaticColumnFieldMappings implements ColumnFieldMappings {
    private Map<String, String> column2field;
    private Map<String, String> field2column;

    StaticColumnFieldMappings(Map<String, String> column2field, Map<String, String> field2column) {
        this.column2field = column2field;
        this.field2column = field2column;
    }

    @Override
    public String fieldToColumn(String fieldName) {
        String s = field2column.get(fieldName);
        return s == null ? fieldName : s;
    }

    @Override
    public String columnToField(String columnName) {
        String s = column2field.get(columnName);
        return s == null ? columnName : s;
    }

    public static final class Builder {
        Builder() {

        }
        private Map<String, String> column2field = new HashMap<>();
        private Map<String, String> field2column = new HashMap<>();
        public Builder mapColumnToField(String columnName, String fieldName) {
            String currentField = column2field.put(columnName, fieldName);
            if (currentField != null) {
                throw new IllegalStateException("Cannot add mapping from column " + columnName + " to field "
                        + fieldName + " because there is already a mapping for this column to field " + currentField
                        + ". Check configuration of PortableMapLoader. There are probably duplicates;");
            }
            String currentColumn = field2column.put(fieldName, columnName);
            if (currentColumn != null) {
                throw new IllegalStateException("Cannot add mapping from column " + columnName + " to field "
                        + fieldName + " because this field is already mapped to a column " + currentColumn
                        + ". Check configuration of PortableMapLoader. There are probably duplicates;");
            }
            return this;
        }

        public ColumnFieldMappings build() {
            if (column2field.isEmpty()) {
                return ColumnFieldMappings.identity();
            }
            return new StaticColumnFieldMappings(column2field, field2column);
        }
    }
}
