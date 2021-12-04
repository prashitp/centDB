package com.example.services.metadata;

import com.example.models.Column;
import com.example.models.Metadata;
import com.example.models.Row;
import com.example.models.TableQuery;
import com.example.models.enums.Entity;
import com.example.models.enums.Operation;
import com.example.services.accessor.FileAccessorImpl;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class DatabaseMetadataServiceImplTest {

    @Test
    public void testDatabaseMetadataService() throws Exception {
        MetadataService metadataService = new DatabaseMetadataServiceImpl();
        Metadata metadata = metadataService.read(Entity.DATABASE, "CENT_DB1");
        metadata.getAllTablesFromDatabase()
                .forEach(table -> {System.out.println("Table:"+ table.getName());
                    table.getColumns().forEach(col -> System.out.println("Column name :" + col.getName()));
                });

        List<String> columns = metadata.getAllColumnsNameForTable("BIRDS");
        List<String> tableName = metadata.getAllTableNames();

        FileAccessorImpl accessor = new FileAccessorImpl();
        Column column1 = new Column();
        column1.getName();
        TableQuery query = TableQuery.builder()
                .schemaName("CENT_DB1")
                .tableName("BIRDS")
                .columns(Collections.singletonList(column1))
                .tableOperation(Operation.SELECT)
                .build();

        List<Row> output = accessor.read(query);
    }
}
