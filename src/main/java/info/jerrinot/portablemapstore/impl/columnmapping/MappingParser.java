package info.jerrinot.portablemapstore.impl.columnmapping;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MappingParser {
    // COLUMN:NAME=FIELD:NAME
    private static final Pattern MAPPING_PATTERN = Pattern.compile("(COLUMN:)(\\w+)(=FIELD:)(\\w+)");

    private MappingParser() {

    }

    public static ColumnFieldMappings createMappings(Properties props) {
        StaticColumnFieldMappings.Builder builder = ColumnFieldMappings.newBuilder();
        for (var key : props.stringPropertyNames()) {
            Matcher matcher = MAPPING_PATTERN.matcher(key);
            if (!matcher.matches()) {
                continue;
            }
            var columnName = matcher.group(2);
            var fieldName = matcher.group(4);
            builder.mapColumnToField(columnName, fieldName);
        }
        return builder.build();
    }
}
