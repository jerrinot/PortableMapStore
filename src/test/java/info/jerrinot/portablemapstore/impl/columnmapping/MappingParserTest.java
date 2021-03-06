package info.jerrinot.portablemapstore.impl.columnmapping;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class MappingParserTest {
    @Test
    public void testEmptyProperties() {
        var props = new Properties();
        ColumnFieldMappings mappings = MappingParser.createMappings(props);

        String field = mappings.columnToField("foo");
        assertEquals("foo", field);

        String column = mappings.fieldToColumn("bar");
        assertEquals("bar", column);
    }

    @Test
    public void testRemapping() {
        var props = new Properties();
        props.setProperty("COLUMN:COLUMNNAME=FIELD:FIELDNAME", "");
        var mappings = MappingParser.createMappings(props);

        var field = mappings.columnToField("COLUMNNAME");
        assertEquals("FIELDNAME", field);
        var column = mappings.fieldToColumn("FIELDNAME");
        assertEquals("COLUMNNAME", column);

        field = mappings.columnToField("foo");
        assertEquals("foo", field);
        column = mappings.fieldToColumn("bar");
        assertEquals("bar", column);
    }

    @Test
    public void testOtherKeys() {
        var props = new Properties();
        props.setProperty("unrelated", "");
        props.setProperty("COLUMN-COLUMNNAME=FIELD:FIELDNAME", "");
        props.setProperty("COLUMN:COLUMNNAME;FIELD:FIELDNAME", "");
        props.setProperty("COLUMN:COLUMNNAME=FIELD-FIELDNAME", "");
        var mappings = MappingParser.createMappings(props);

        var fieldName = mappings.columnToField("COLUMNNAME");
        assertEquals("COLUMNNAME", fieldName);
        var columnName = mappings.fieldToColumn("FIELDNAME");
        assertEquals("FIELDNAME", columnName);
    }
}