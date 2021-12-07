package com.example.models.erd;

import com.example.models.Metadata;
import com.example.models.enums.Entity;
import com.example.services.metadata.MetadataService;
import com.example.services.metadata.MetadataServiceImpl;

public class Erd {
    public static void main(String[] args) {
        MetadataService metadataService = new MetadataServiceImpl();
        Metadata metadata = metadataService.read(Entity.DATABASE, "CENT_DB1");
        metadata.getAllTablesFromDatabase()
                .forEach(table -> {
                    System.out.printf("-----------------------------------------------------------------------------------------------------------------------");
                    System.out.printf("\n%60s%n", "Table Name:"+table.getName());
                    System.out.printf("-----------------------------------------------------------------------------------------------------------------------");
                    System.out.printf("\n%-15s |", "Column Names");
                    table.getColumns().forEach(col ->
                           System.out.printf("%20s |", col.getName())
                   );
                    System.out.printf("\n-----------------------------------------------------------------------------------------------------------------------");
                    System.out.printf("\n%-15s |", "Column Type");
                    table.getColumns().forEach(col ->
                                    System.out.printf("%20s |", col.getDataType())
                    );
                    System.out.printf("\n-----------------------------------------------------------------------------------------------------------------------");
                    System.out.printf("\n%-15s |", "Primary Key");
                    table.getColumns().forEach(col -> {
                        if (table.getPrimaryKey().getName().equals(col.getName())) {
                            System.out.printf("%20s |", "PK");
                        }
                        else {
                            System.out.printf("%20s |", "");
                        }
                            }
                    );
                    System.out.printf("\n-----------------------------------------------------------------------------------------------------------------------");
                    System.out.printf("\n\n\n");
                });
    }
}

