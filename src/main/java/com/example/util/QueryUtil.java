package com.example.util;

import com.example.models.Column;
import com.example.models.Metadata;
import com.example.models.Table;

public class QueryUtil {

    public static Table getTable(Metadata metadata, String tableName) {
        return metadata.getAllTablesFromDatabase()
                .stream()
                .filter(data -> data.getName().equalsIgnoreCase(tableName))
                .findFirst()
                .get();
    }

    public static Column getColumn(Table table, String columnName) {
        return table.getColumns()
                .stream()
                .filter(data -> data.getName().equalsIgnoreCase(columnName))
                .findFirst()
                .get();
    }
}
