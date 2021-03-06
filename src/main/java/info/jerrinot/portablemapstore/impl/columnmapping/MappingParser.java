package info.jerrinot.portablemapstore.impl.columnmapping;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MappingParser {
    // COLUMN:NAME=FIELD:NAME
    // TODO: allow numbers in column/field names
    private static final Pattern MAPPING_PATTERN = Pattern.compile("(\\w+:)(\\w+)(=)(\\w+:)(\\w+)");

    private MappingParser() {

    }

    public static ColumnFieldMappings createMappings(Properties props) {
        StaticColumnFieldMappings.Builder builder = ColumnFieldMappings.newBuilder();
        for (var entry : props.entrySet()) {
            var keyObject = entry.getKey();
            if (!(keyObject instanceof String)) {
                continue;
            }
            String key = (String) keyObject;
            Matcher matcher = MAPPING_PATTERN.matcher(key);
            if (!matcher.matches()) {
                continue;
            }
            var columnName = matcher.group(2);
            var fieldName = matcher.group(5);
            builder.mapColumnToField(columnName, fieldName);
        }
        return builder.build();
    }
}
