package com.example.services.accessor;

import com.example.models.*;
import com.example.models.enums.Datatype;
import com.example.models.enums.Operation;
import com.example.models.enums.Operator;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class FileAccessorImplTest extends TestCase {

    private static final String SCHEMA_NAME = "CENT_DB1";
    private static final String TABLE_NAME = "BIRDS";

    @Test
    public void testSelectWithoutCondition() throws Exception {
        FileAccessorImpl accessor = new FileAccessorImpl();
        Column column1 = new Column();
        column1.setName("BIRD_ID");
        Column column2 = new Column();
        column2.setName("SCIENTIFIC_NAME");
        Column column3 = new Column();
        column3.setName("COMMON_NAME");
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .columns(Arrays.asList(column1, column2, column3)).tableOperation(Operation.SELECT).build();
        List<Row> output = accessor.read(query);
        Assert.assertTrue(output.size() >= 6);
        Row row = output.get(0);
        assertEquals(3, row.getAllFieldsOfRow().size());
    }

    @Test
    public void testSelectWithVarcharCondition() throws Exception {
        FileAccessorImpl accessor = new FileAccessorImpl();
        Column column1 = new Column();
        column1.setName("COMMON_NAME");
        Column column2 = new Column();
        column2.setName("SCIENTIFIC_NAME");
        Condition condition = Condition.builder().operand1("COMMON_NAME").operator(Operator.EQUALS).operand2("BirdCommonName2").build();
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .columns(Arrays.asList(column1, column2)).tableOperation(Operation.SELECT).conditions(List.of(condition)).build();
        List<Row> output = accessor.read(query);
        Assert.assertTrue(output.size() == 1);
        Row row = output.get(0);
        assertEquals(2, row.getAllFieldsOfRow().size());
        Assert.assertEquals("BirdCommonName2", row.getFieldByColumnName("COMMON_NAME").getValue());
    }

    @Test
    public void testSelectWithEqualsIntegerCondition() throws Exception {
        FileAccessorImpl accessor = new FileAccessorImpl();
        Column column1 = new Column();
        column1.setName("BIRD_ID");
        Column column2 = new Column();
        column2.setName("SCIENTIFIC_NAME");
        Condition condition = Condition.builder().operand1("BIRD_ID").operator(Operator.EQUALS).operand2("5").build();
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .columns(Arrays.asList(column1, column2)).tableOperation(Operation.SELECT).conditions(List.of(condition)).build();
        List<Row> output = accessor.read(query);
        Assert.assertTrue(output.size() == 1);
        Row row = output.get(0);
        assertEquals(2, row.getAllFieldsOfRow().size());
        Assert.assertEquals("5", row.getFieldByColumnName("BIRD_ID").getValue());
        Assert.assertEquals("BirdScientificName5", row.getFieldByColumnName("SCIENTIFIC_NAME").getValue());
    }

    @Test
    public void testSelectWithGreaterThanIntegerCondition() throws Exception {
        FileAccessorImpl accessor = new FileAccessorImpl();
        Column column1 = new Column();
        column1.setName("BIRD_ID");
        Column column2 = new Column();
        column2.setName("SCIENTIFIC_NAME");
        Condition condition = Condition.builder().operand1("BIRD_ID").operator(Operator.GREATER_THAN).operand2("2").build();
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .columns(Arrays.asList(column1, column2)).tableOperation(Operation.SELECT).conditions(List.of(condition)).build();
        List<Row> output = accessor.read(query);
        Assert.assertTrue(output.size() >= 4);
        Row row = output.get(0);
        assertEquals(2, row.getAllFieldsOfRow().size());
    }

    @Test
    public void testSelectWithLessThanIntegerCondition() throws Exception {
        FileAccessorImpl accessor = new FileAccessorImpl();
        Column column1 = new Column();
        column1.setName("BIRD_ID");
        Column column2 = new Column();
        column2.setName("SCIENTIFIC_NAME");
        Column column3 = new Column();
        column3.setName("COMMON_NAME");
        Condition condition = Condition.builder().operand1("BIRD_ID").operator(Operator.LESS_THAN).operand2("2").build();
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .columns(Arrays.asList(column1, column2, column3)).tableOperation(Operation.SELECT).conditions(List.of(condition)).build();
        List<Row> output = accessor.read(query);
        Assert.assertTrue(output.size() == 1);
        Row row = output.get(0);
        assertEquals(3, row.getAllFieldsOfRow().size());
        Assert.assertEquals("1", row.getFieldByColumnName("BIRD_ID").getValue());
        Assert.assertEquals("BirdScientificName1", row.getFieldByColumnName("SCIENTIFIC_NAME").getValue());
    }

    @Test
    public void testInsertToTable() throws Exception {
        FileAccessorImpl accessor = new FileAccessorImpl();
        Column column1 = new Column();
        column1.setName("BIRD_ID");
        column1.setDataType(Datatype.INTEGER.name());
        Column column2 = new Column();
        column2.setName("SCIENTIFIC_NAME");
        column2.setDataType(Datatype.VARCHAR.name());
        Column column3 = new Column();
        column3.setName("COMMON_NAME");
        column3.setDataType(Datatype.VARCHAR.name());
        Row row = new Row();
        row.addField(new Field(column1, "7"));
        row.addField(new Field(column2, "Gavia stellata"));
        row.addField(new Field(column3, "Red-throated diver"));
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .rows(List.of(row)).tableOperation(Operation.INSERT).build();
        List<Row> output = accessor.insert(query);
    }

    @Test
    public void testInsertWithColumnValueHavingDelimiter() throws Exception {
        FileAccessorImpl accessor = new FileAccessorImpl();
        Column column1 = new Column();
        column1.setName("BIRD_ID");
        column1.setDataType(Datatype.INTEGER.name());
        Column column2 = new Column();
        column2.setName("SCIENTIFIC_NAME");
        column2.setDataType(Datatype.VARCHAR.name());
        Column column3 = new Column();
        column3.setName("COMMON_NAME");
        column3.setDataType(Datatype.VARCHAR.name());
        Row row = new Row();
        row.addField(new Field(column1, "8"));
        row.addField(new Field(column2, "Morus|bassanus"));
        row.addField(new Field(column3, "Gannet"));
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .rows(List.of(row)).tableOperation(Operation.INSERT).build();
        List<Row> output = accessor.insert(query);
    }

    @Test
    public void testSelectWithValueHavingDelimiter() throws Exception {
        FileAccessorImpl accessor = new FileAccessorImpl();
        Column column1 = new Column();
        column1.setName("BIRD_ID");
        Column column2 = new Column();
        column2.setName("SCIENTIFIC_NAME");
        Column column3 = new Column();
        column3.setName("COMMON_NAME");
        Condition condition = Condition.builder().operand1("SCIENTIFIC_NAME").operator(Operator.EQUALS).operand2("Morus|bassanus").build();
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .columns(Arrays.asList(column1, column2, column3)).tableOperation(Operation.SELECT).conditions(List.of(condition)).build();
        List<Row> output = accessor.read(query);
        Assert.assertTrue(output.size() == 1);
        Row row = output.get(0);
        assertEquals(3, row.getAllFieldsOfRow().size());
        Assert.assertEquals("Morus|bassanus", row.getFieldByColumnName("SCIENTIFIC_NAME").getValue());
    }

    @Test
    public void testInsertIfAllValuesNotProvided() throws Exception {
        FileAccessorImpl accessor = new FileAccessorImpl();
        Column column1 = new Column();
        column1.setName("BIRD_ID");
        column1.setDataType(Datatype.INTEGER.name());
        Column column3 = new Column();
        column3.setName("COMMON_NAME");
        column3.setDataType(Datatype.VARCHAR.name());
        Row row = new Row();
        row.addField(new Field(column1, "9"));
        row.addField(new Field(column3, "Cormorant"));
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .rows(List.of(row)).tableOperation(Operation.INSERT).build();
        List<Row> output = accessor.insert(query);
    }

    @Test
    public void testInsertMultipleRows() throws Exception {
        FileAccessorImpl accessor = new FileAccessorImpl();
        Row row1 = new Row();
        row1.addField(new Field(new Column("BIRD_ID", Datatype.INTEGER.name()), "10"));
        row1.addField(new Field(new Column("SCIENTIFIC_NAME", Datatype.VARCHAR.name()), "Cygnus olor"));
        row1.addField(new Field(new Column("COMMON_NAME", Datatype.VARCHAR.name()), "Mute swan"));

        Row row2 = new Row();
        row2.addField(new Field(new Column("BIRD_ID", Datatype.INTEGER.name()), "11"));
        row2.addField(new Field(new Column("COMMON_NAME", Datatype.VARCHAR.name()), "Bean goose"));

        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .rows(List.of(row1, row2)).tableOperation(Operation.INSERT).build();
        List<Row> output = accessor.insert(query);
    }

    @Test(expected = Exception.class)
    public void testMalformedQuery() throws Exception {
        FileAccessorImpl accessor = new FileAccessorImpl();
        Row row1 = new Row();
        row1.addField(new Field());

        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .rows(List.of(row1)).tableOperation(Operation.INSERT).build();
        List<Row> output = accessor.insert(query);
    }

}