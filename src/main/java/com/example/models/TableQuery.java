package com.example.models;

import com.example.models.enums.TableDMLOperation;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class TableQuery {

    public TableQuery(String schemaName, String tableName, List<Column> requiredColumns, TableDMLOperation tableOperation) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columns = requiredColumns;
        this.tableOperation = tableOperation;
    }

//    database Name in which the table is present
    private String schemaName;

//    Name of the table from which data is to be fetched
    private String tableName;

//    Columns that are required
    private List<Column> columns;

    private TableDMLOperation tableOperation;

    private List<Condition> conditions;

    public String getTableName() {
        return tableName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public TableDMLOperation getTableOperation() {
        return tableOperation;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    private void addCondition(Condition condition) {
        if (Objects.isNull(conditions)) {
            conditions = new ArrayList<>();
        }
        conditions.add(condition);
    }

    private void addColumns(Column columnName) {
        if (Objects.isNull(columns)) {
            columns = new ArrayList<>();
        }
        columns.add(columnName);
    }

    public String getSchemaName() {
        return schemaName;
    }

}
