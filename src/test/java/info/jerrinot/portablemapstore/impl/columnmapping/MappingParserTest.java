package info.jerrinot.portablemapstore.impl.columnmapping;

import com.hazelcast.config.InvalidConfigurationException;
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

    @Test
    public void testPropertyIsNotStrint() {
        var props = new Properties();
        props.put(new Object(), "");
        props.setProperty("COLUMN:COLUMNNAME=FIELD:FIELDNAME", "");
        var mappings = MappingParser.createMappings(props);

        assertEquals("FIELDNAME", mappings.columnToField("COLUMNNAME"));
    }

    @Test
    public void testNumbersInColumnFieldNames() {
        var props = new Properties();
        props.put(new Object(), "");
        props.setProperty("COLUMN:1COLUMN2NAME3=FIELD:1FIELD2NAME3", "");
        var mappings = MappingParser.createMappings(props);

        assertEquals("1FIELD2NAME3", mappings.columnToField("1COLUMN2NAME3"));
        assertEquals("1COLUMN2NAME3", mappings.fieldToColumn("1FIELD2NAME3"));
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testMappingForAlreadyMappedColumn() {
        var props = new Properties();
        props.put(new Object(), "");
        props.setProperty("COLUMN:COLUMNNAME=FIELD:FIELDNAME", "");
        props.setProperty("COLUMN:COLUMNNAME=FIELD:OTHER_FIELDNAME", "");

        MappingParser.createMappings(props);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testMappingForAlreadyMappedField() {
        var props = new Properties();
        props.put(new Object(), "");
        props.setProperty("COLUMN:COLUMNNAME=FIELD:FIELDNAME", "");
        props.setProperty("COLUMN:OTHER_COLUMNNAME=FIELD:FIELDNAME", "");

        MappingParser.createMappings(props);
    }

}