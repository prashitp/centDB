package com.example.models;

import com.example.models.enums.Operation;
import com.example.services.accessor.FileAccessorImpl;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

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
        if (Objects.isNull(database.getTables()) || database.getTables().isEmpty()) {
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

    public Table getTableByName(String tableName) {
        Table table;
        if (Objects.isNull(database.getTables()) || database.getTables().isEmpty()) {
            return null;
        } else {
            Optional<Table> optionalTable = database.getTables().stream().filter(t -> t.getName().equalsIgnoreCase(tableName))
                    .findFirst();
            table = optionalTable.isPresent() ? optionalTable.get() : null;
        }
        return table;
    }
}
