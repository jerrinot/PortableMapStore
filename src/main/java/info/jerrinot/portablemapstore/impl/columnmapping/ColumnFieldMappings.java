package info.jerrinot.portablemapstore.impl.columnmapping;

public interface ColumnFieldMappings {
    ColumnFieldMappings IDENTITY_MAPPING = new ColumnFieldMappings() {
        @Override
        public String fieldToColumn(String fieldName) {
            return fieldName;
        }

        @Override
        public String columnToField(String columnName) {
            return columnName;
        }
    };

    String fieldToColumn(String fieldName);

    String columnToField(String columnName);

    static StaticColumnFieldMappings.Builder newBuilder() {
        return new StaticColumnFieldMappings.Builder();
    }

    static ColumnFieldMappings identity() {
        return IDENTITY_MAPPING;
    }
}
