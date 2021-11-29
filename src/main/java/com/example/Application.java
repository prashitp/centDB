package com.example;


import com.example.models.*;
import com.example.services.dataAccessor.IFileDataAccessorImpl;
import com.example.services.metadata.DatabaseMetadataService;
import com.example.services.metadata.IMetadataService;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {

        final Scanner scanner = new Scanner(System.in);

//        testMethods();

    }

//    Methods to test the output and this is to removed later
    private static void testMethods() {
        IMetadataService mdService = new DatabaseMetadataService();
        Metadata metadata = mdService.readMetadataForDatabase("CENT_DB1");
        metadata.getAllTablesFromDatabase().stream()
                .forEach(table -> {System.out.println("Table:"+ table.getName());
                    table.getColumns().forEach(col -> System.out.println("Column name :" + col.getName()));
                });

        List<String> cols = metadata.getAllColumnsNameForTable("BIRDS");
        List<String> tableName = metadata.getAllTableNames();

        IFileDataAccessorImpl accessor = new IFileDataAccessorImpl();
        Column column1 = new Column();
        column1.getName();
        TableQuery query = new TableQuery("CENT_DB1", "BIRDS", Arrays.asList(column1), TableDMLOperation.SELECT);
        List<Row> output = accessor.readDataFromTable(query);
    }
}
