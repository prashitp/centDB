package com.example.services.accessor;

import com.example.models.*;
import com.example.models.enums.Datatype;
import com.example.models.enums.Entity;
import com.example.models.enums.Operation;
import com.example.models.enums.Operator;
import com.example.services.metadata.MetadataService;
import com.example.services.metadata.MetadataServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileAccessorImplTest {

    private static final String SCHEMA_NAME = "CENT_DB1";
    private static final String TABLE_NAME = "BIRDS";

    private FileAccessorImpl accessor = new FileAccessorImpl();

    @Test
    public void testSelectWithoutCondition() throws Exception {
//        SELECT * FROM BIRDS;
        Column column1 = new Column();
        column1.setName("BIRD_ID");
        Column column2 = new Column();
        column2.setName("SCIENTIFIC_NAME");
        Column column3 = new Column();
        column3.setName("COMMON_NAME");
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .columns(Arrays.asList(column1, column2, column3)).tableOperation(Operation.SELECT).build();
        List<Row> output = accessor.read(query);
        Assert.assertTrue(output.size() >= 5);
        Row row = output.get(0);
        assertEquals(3, row.getAllFieldsOfRow().size());
    }

    @Test
    public void testSelectWithVarcharCondition() throws Exception {
//        SELECT COMMON_NAME, SCIENTIFIC_NAME WHERE COMMON_NAME = 'BirdCommonName2'
        FileAccessorImpl accessor = new FileAccessorImpl();
        Column column1 = new Column();
        column1.setName("COMMON_NAME");
        Column column2 = new Column();
        column2.setName("SCIENTIFIC_NAME");
        Condition condition = Condition.builder().operand1("COMMON_NAME").operator(Operator.EQUALS).operand2("BirdCommonName2").build();
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .columns(Arrays.asList(column1, column2)).tableOperation(Operation.SELECT).conditions(List.of(condition)).build();
        List<Row> output = accessor.read(query);
        assertEquals(1, output.size());
        Row row = output.get(0);
        assertEquals(2, row.getAllFieldsOfRow().size());
        assertEquals("BirdCommonName2", row.getFieldByColumnName("COMMON_NAME").getValue());
    }

    @Test
    public void testSelectWithVarcharNullCondition() throws Exception {
//        SELECT BIRD_ID, SCIENTIFIC_NAME FROM BIRDS WHERE SCIENTIFIC_NAME = NULL
        FileAccessorImpl accessor = new FileAccessorImpl();
        Column column1 = new Column();
        column1.setName("BIRD_ID");
        Column column2 = new Column();
        column2.setName("SCIENTIFIC_NAME");
        Column column3 = new Column();
        column3.setName("COMMON_NAME");
        Condition condition = Condition.builder().operand1("SCIENTIFIC_NAME").operator(Operator.EQUALS).operand2("NULL").build();
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .columns(Arrays.asList(column1, column2, column3)).tableOperation(Operation.SELECT).conditions(List.of(condition)).build();
        List<Row> output = accessor.read(query);
        Assert.assertTrue(output.size() >= 1);
        Row row = output.get(0);
        assertEquals(3, row.getAllFieldsOfRow().size());
        assertEquals("BirdCommonName4", row.getFieldByColumnName("COMMON_NAME").getValue());
    }

    @Test
    public void testSelectWithEqualsIntegerCondition() throws Exception {
//        SELECT BIRD_ID, SCIENTIFIC_NAME FROM BIRDS WHERE BIRD_ID = 5
        FileAccessorImpl accessor = new FileAccessorImpl();
        Column column1 = new Column();
        column1.setName("BIRD_ID");
        Column column2 = new Column();
        column2.setName("SCIENTIFIC_NAME");
        Condition condition = Condition.builder().operand1("BIRD_ID").operator(Operator.EQUALS).operand2("5").build();
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .columns(Arrays.asList(column1, column2)).tableOperation(Operation.SELECT).conditions(List.of(condition)).build();
        List<Row> output = accessor.read(query);
        assertEquals(1, output.size());
        Row row = output.get(0);
        assertEquals(2, row.getAllFieldsOfRow().size());
        assertEquals("5", row.getFieldByColumnName("BIRD_ID").getValue());
        assertEquals("BirdScientificName5", row.getFieldByColumnName("SCIENTIFIC_NAME").getValue());
    }

    @Test
    public void testSelectWithGreaterThanIntegerCondition() throws Exception {
//        SELECT BIRD_ID, SCIENTIFIC_NAME FROM BIRDS WHERE BIRD_ID > 2
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
//        SELECT BIRD_ID, SCIENTIFIC_NAME, COMMON_NAME FROM BIRDS WHERE BIRD_ID < 2
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
        assertEquals(1, output.size());
        Row row = output.get(0);
        assertEquals(3, row.getAllFieldsOfRow().size());
        assertEquals("1", row.getFieldByColumnName("BIRD_ID").getValue());
        assertEquals("BirdScientificName1", row.getFieldByColumnName("SCIENTIFIC_NAME").getValue());
    }

    @Test
    public void testInsertToTable() throws Exception {
//        INSERT INTO BIRDS ('BIRD_ID', 'SCIENTIFIC_NAME', 'COMMON_NAME') VALUES (7, 'Gavia stellata', 'Red-throated diver');
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
//        INSERT INTO BIRDS ('BIRD_ID', 'SCIENTIFIC_NAME', 'COMMON_NAME') VALUES (7, 'Morus|bassanus', 'Gannet');
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
//        SELECT BIRD_ID, SCIENTIFIC_NAME, COMMON_NAME FROM BIRDS WHERE SCIENTIFIC_NAME = 'Morus|bassanus'
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
        assertEquals(1, output.size());
        Row row = output.get(0);
        assertEquals(3, row.getAllFieldsOfRow().size());
        assertEquals("Morus|bassanus", row.getFieldByColumnName("SCIENTIFIC_NAME").getValue());
    }

    @Test
    public void testInsertIfAllValuesNotProvided() throws Exception {
//        INSERT INTO BIRDS ('BIRD_ID' , 'COMMON_NAME') VALUES (9, 'Cormorant');
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
//       INSERT INTO BIRDS ('BIRD_ID' , 'SCIENTIFIC_NAME', 'COMMON_NAME')
//                  VALUES (10, 'Cygnus olor', 'Cormorant'),
//                         (11, 'Bean goose', NULL);
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

    @Test
    public void testDeleteQuery() throws Exception {
//        DELETE FROM BIRDS WHERE SCIENTIFIC_NAME = 'BirdScientificName5'
        FileAccessorImpl accessor = new FileAccessorImpl();
        Condition condition = Condition.builder().operand1("SCIENTIFIC_NAME").operator(Operator.EQUALS).operand2("BirdScientificName5").build();
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME)
                .tableOperation(Operation.DELETE).conditions(List.of(condition)).build();
        List<Row> output = accessor.delete(query);
    }

    @Test
    public void testUpdateQuery() throws Exception {
//        UPDATE BIRDS SET SCIENTIFIC_NAME='BirdScientificName6' WHERE BIRD_ID=6
        FileAccessorImpl accessor = new FileAccessorImpl();
        Condition condition = Condition.builder().operand1("BIRD_ID").operator(Operator.EQUALS).operand2("6").build();
        Field field = new Field(new Column("SCIENTIFIC_NAME", Datatype.VARCHAR.name()), "ScientificName6");
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME).fields(List.of(field))
                .tableOperation(Operation.UPDATE).conditions(List.of(condition)).build();
        List<Row> output = accessor.update(query);
    }

    @Test
    public void testUpdateQueryWithMultipleSet() throws Exception {
//        UPDATE BIRDS SET SCIENTIFIC_NAME='BirdScientificName6' WHERE BIRD_ID=6
        FileAccessorImpl accessor = new FileAccessorImpl();
        Condition condition = Condition.builder().operand1("BIRD_ID").operator(Operator.EQUALS).operand2("6").build();
        Field field1 = new Field(new Column("COMMON_NAME", Datatype.VARCHAR.name()), "BirdCommonName6");
        Field field2 = new Field(new Column("SCIENTIFIC_NAME", Datatype.VARCHAR.name()), "BirdScientificName6");
        TableQuery query = TableQuery.builder().schemaName(SCHEMA_NAME).tableName(TABLE_NAME).fields(List.of(field1, field2))
                .tableOperation(Operation.UPDATE).conditions(List.of(condition)).build();
        List<Row> output = accessor.update(query);
    }

    @Test
    public void createTable() throws Exception {
//        CREATE TABLE STUDENT
            String dbName = "DB2";
            String tableName = "STUDENT";
            String INTEGER = "INTEGER";
            String VARCHAR ="VARCHAR";
            Column id = Column.builder().name("ID").dataType(INTEGER).build();
            Column firstName = Column.builder().name("FIRST_NAME").dataType(VARCHAR).build();
            Column lastName = Column.builder().name("LAST_NAME").dataType(VARCHAR).build();
            Column email = Column.builder().name("EMAIL_ID").dataType(VARCHAR).build();
            List<Column> columns = Arrays.asList(id, firstName, lastName, email);
            Table table = Table.builder().name(tableName).columns(columns).primaryKey(id).build();
            TableQuery query = TableQuery.builder().schemaName(dbName).tableName(tableName).table(table).build();
            query.setTableOperation(Operation.CREATE);
            accessor.create(query);
    }

    @Test
    public void dropTable() throws Exception {
//        DROP TABLE STUDENT
        String dbName = "DB2";
        String tableName = "STUDENT";
        String INTEGER = "INTEGER";
        String VARCHAR ="VARCHAR";
        Column id = Column.builder().name("ID").dataType(INTEGER).build();
        Column firstName = Column.builder().name("FIRST_NAME").dataType(VARCHAR).build();
        Column lastName = Column.builder().name("LAST_NAME").dataType(VARCHAR).build();
        Column email = Column.builder().name("EMAIL_ID").dataType(VARCHAR).build();
        Database database = Database.builder().name(dbName).build();
        List<Column> columns = Arrays.asList(id, firstName, lastName, email);
        Table table = Table.builder().name(tableName).columns(columns).primaryKey(id).build();
        database.setTables(Arrays.asList(table));
        TableQuery query = TableQuery.builder().schemaName(dbName).tableName(tableName).columns(columns).build();
        query.setTableOperation(Operation.DROP);
        accessor.drop(query);
    }

}