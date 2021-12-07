package com.example.services.metadata;

import com.example.models.*;
import com.example.models.enums.Entity;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MetadataServiceImplTest {

    @Test
    @SneakyThrows
    public void testDatabaseMetadataService() {
        MetadataService metadataService = new MetadataServiceImpl();
        Metadata metadata = metadataService.read(Entity.DATABASE, "CENT_DB1");
        metadata.getAllTablesFromDatabase()
                .forEach(table -> {
                    System.out.println("Table:"+ table.getName());
                    table.getColumns().forEach(col -> System.out.println("Column name :" + col));
                    System.out.println("Primary Key:" + table.getPrimaryKey());
                    Optional.ofNullable(table.getForeignKeys())
                            .ifPresent(foreignKeys -> foreignKeys.forEach((foreignKey -> {
                        System.out.println("Foreign Key Column Name:" + foreignKey.getForeignKeyColumn());
                        System.out.println("Foreign Key Column Reference table:" + foreignKey.getReferenceTableName());
                        System.out.println("Foreign Key Column Reference column:" + foreignKey.getReferenceColumnName());
                    })));
                });
        List<String> columns = metadata.getAllColumnsNameForTable("BIRDS");
        List<String> tableName = metadata.getAllTableNames();
    }

    @Test
    public void createDatabase() throws Exception {
        String dbName = "DB2";
        Database database = Database.builder().name(dbName).build();
        Metadata metadata = new Metadata();
        metadata.setDatabase(database);
        MetadataService metadataService = new MetadataServiceImpl();
        metadataService.write(Entity.DATABASE, metadata);
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
        Database database = Database.builder().name(dbName).build();
        List<Column> columns = Arrays.asList(id, firstName, lastName, email);
        Table table = Table.builder().name(tableName).columns(columns).primaryKey(id).build();
        database.setTables(Arrays.asList(table));
        Metadata metadata = new Metadata();
        metadata.setDatabase(database);
        MetadataService metadataService = new MetadataServiceImpl();
        metadataService.write(Entity.TABLE, metadata);
    }

    @Test
    public void createTableWithForeignKey() throws Exception {
//        CREATE TABLE STUDENT_CONTACT
        String dbName = "DB2";
        String tableName = "STUDENT_CONTACT";
        String INTEGER = "INTEGER";
        String VARCHAR ="VARCHAR";
        Column contactId = Column.builder().name("CONTACT_ID").dataType(INTEGER).build();
        Column phoneNumber = Column.builder().name("PHONE_NUMBER").dataType(VARCHAR).build();
        Column studentId = Column.builder().name("STUDENT_ID").dataType(VARCHAR).build();
        ForeignKey foreignKey = ForeignKey.builder().foreignKeyColumn("STUDENT_ID")
                                                    .referenceTableName("STUDENT")
                                                    .referenceColumnName("ID").build();
        Database database = Database.builder().name(dbName).build();
        List<Column> columns = Arrays.asList(contactId, phoneNumber, studentId);
        Table table = Table.builder().name(tableName).columns(columns).primaryKey(contactId).foreignKeys(Arrays.asList(foreignKey)).build();
        database.setTables(Arrays.asList(table));
        Metadata metadata = new Metadata();
        metadata.setDatabase(database);
        MetadataService metadataService = new MetadataServiceImpl();
        metadataService.write(Entity.TABLE, metadata);
    }

    @Test
    public void deleteTable() throws Exception {
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
        Metadata metadata = new Metadata();
        metadata.setDatabase(database);
        MetadataService metadataService = new MetadataServiceImpl();
        metadataService.delete(Entity.TABLE, metadata);
    }

    @Test
    public void deleteTableWithForeignKey() throws Exception {
//        DROP TABLE STUDENT_CONTACT
        String dbName = "DB2";
        String tableName = "STUDENT_CONTACT";
        String INTEGER = "INTEGER";
        String VARCHAR ="VARCHAR";
        Column contactId = Column.builder().name("CONTACT_ID").dataType(INTEGER).build();
        Column phoneNumber = Column.builder().name("PHONE_NUMBER").dataType(VARCHAR).build();
        Column studentId = Column.builder().name("STUDENT_ID").dataType(VARCHAR).build();
        ForeignKey foreignKey = ForeignKey.builder().foreignKeyColumn("STUDENT_ID")
                .referenceTableName("STUDENT")
                .referenceColumnName("ID").build();
        Database database = Database.builder().name(dbName).build();
        List<Column> columns = Arrays.asList(contactId, phoneNumber, studentId);
        Table table = Table.builder().name(tableName).columns(columns).primaryKey(contactId).foreignKeys(Arrays.asList(foreignKey)).build();
        database.setTables(Arrays.asList(table));
        Metadata metadata = new Metadata();
        metadata.setDatabase(database);
        MetadataService metadataService = new MetadataServiceImpl();
        metadataService.delete(Entity.TABLE, metadata);
    }

    @Test
    public void deleteDatabase() throws Exception {
        String dbName = "DB2";
        Database database = Database.builder().name(dbName).build();
        Metadata metadata = new Metadata();
        metadata.setDatabase(database);
        MetadataService metadataService = new MetadataServiceImpl();
        metadataService.delete(Entity.DATABASE, metadata);
    }

}
