package com.example.services.metadata;

import com.example.models.Column;
import com.example.models.Metadata;
import com.example.models.Row;
import com.example.models.TableQuery;
import com.example.models.enums.Entity;
import com.example.models.enums.Operation;
import com.example.services.accessor.FileDataAccessorImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class DatabaseMetadataServiceImplTest {

    @Test
    public void testDatabaseMetadataService() {
        MetadataService metadataService = new DatabaseMetadataServiceImpl();
        Metadata metadata = metadataService.read(Entity.DATABASE, "CENT_DB1");
        metadata.getAllTablesFromDatabase()
                .forEach(table -> {System.out.println("Table:"+ table.getName());
                    table.getColumns().forEach(col -> System.out.println("Column name :" + col.getName()));
                });

        List<String> columns = metadata.getAllColumnsNameForTable("BIRDS");
        List<String> tableName = metadata.getAllTableNames();

        FileDataAccessorImpl accessor = new FileDataAccessorImpl();
        Column column1 = new Column();
        column1.getName();
        TableQuery query = new TableQuery("CENT_DB1", "BIRDS", Arrays.asList(column1), Operation.SELECT);
        List<Row> output = accessor.readDataFromTable(query);
    }
}
