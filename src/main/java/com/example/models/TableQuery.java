package com.example.models;

import com.example.models.enums.Operation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
public class TableQuery {

    private String schemaName;

    private String tableName;

    private Table table;

    private List<Column> columns;

    private List<Field> fields;

    private Operation tableOperation;

    private List<Condition> conditions;

    private List<Row> rows;

    public String getTableName() {
        return tableName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Operation getTableOperation() {
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

    /*
     *    The rows added here represents all the rows that will be affected by the
     *    Operation specified in this query
     */
    private void addRows(List<Row> rows) {
        if (Objects.nonNull(rows)) {
            rows = new ArrayList<>();
        }
        rows.addAll(rows);
    }

    public String getSchemaName() {
        return schemaName;
    }

}
