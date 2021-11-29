package com.example.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
public class Metadata {

    private Database database;

    public String getDatabaseName() {
        return database.getName();
    }

    public List<Table> getAllTablesFromDatabase() {
        return database.getTables();
    }

    public List<String> getAllTableNames() {
        List<String> tableNames = new ArrayList<>();
        if(!database.getTables().isEmpty()) {
            database.getTables().forEach(table -> tableNames.add(table.getName()));
        }
        return tableNames;
    }

    public List<Column> getAllColumnsForTable(String tableName) {
        List<Column> columns = new ArrayList<>();
        if (database.getTables().isEmpty()) {
            return columns;
        }
        Optional<Table> table = database.getTables().stream().filter(t -> t.getName().equalsIgnoreCase(tableName)).findFirst();
        if (table.isPresent()) {
            columns = table.get().getColumns();
        }
        return columns;
    }

    public List<String> getAllColumnsNameForTable(String tableName) {
        List<String> columnNames = new ArrayList<>();
        if(!database.getTables().isEmpty()) {
            Optional<Table> table = database.getTables().stream().filter(t -> t.getName().equalsIgnoreCase(tableName)).findFirst();
            table.ifPresent(t -> t.getColumns().forEach(c -> columnNames.add(c.getName())));
        }
        return columnNames;
    }

}
