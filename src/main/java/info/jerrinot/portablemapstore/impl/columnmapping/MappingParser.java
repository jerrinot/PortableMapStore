package info.jerrinot.portablemapstore.impl.columnmapping;

import java.util.Properties;

public final class MappingParser {
    private MappingParser() {

    }

    public static ColumnFieldMappings createMappings(Properties props) {
        // COLUMN:NAME=FIELD:NAME
        StaticColumnFieldMappings.Builder builder = ColumnFieldMappings.newBuilder();
        for (var entry : props.entrySet()) {
            String key = (String) entry.getKey();
            if (!key.startsWith("COLUMN:")) {
                continue;
            }
            String withoutPrefix = key.substring("COLUMN:".length());
            String[] split = withoutPrefix.split("=");
            if (split.length != 2) {
                continue;
            }
            String fieldNameWithPrefix = split[1];
            if (!fieldNameWithPrefix.startsWith("FIELD:")) {
                continue;
            }
            String fieldName = fieldNameWithPrefix.substring("FIELD:".length());
            String columnName = split[0];
            builder.mapColumnToField(columnName, fieldName);
        }
        return builder.build();
    }
}
