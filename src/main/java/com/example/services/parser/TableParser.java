package com.example.services.parser;

import com.example.models.*;
import com.example.models.enums.Operation;
import com.example.models.enums.Operator;
import com.example.util.QueryUtil;
import com.example.util.StringUtil;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.util.Constants.*;
import static com.example.util.Constants.FROM;

@AllArgsConstructor
public class TableParser {

    public TableQuery select(String query, Metadata metadata) {
        Table table = QueryUtil.getTable(metadata, getTable(query, FROM));
        Condition condition = getCondition(query);
        List<Column> columns = getColumns(StringUtil.match(query, SELECT, FROM),
                table.getColumns());

        return TableQuery.builder()
                .schemaName(metadata.getDatabaseName())
                .tableName(table.getName())
                .columns(columns)
                .tableOperation(Operation.SELECT)
                .conditions(Objects.nonNull(condition) ? Collections.singletonList(condition) : Collections.EMPTY_LIST)
                .build();
    }

    public TableQuery delete(String query, Metadata metadata) {
        Table table = QueryUtil.getTable(metadata, getTable(query, FROM));
        Condition condition = getCondition(query);

        return TableQuery.builder()
                .schemaName(metadata.getDatabaseName())
                .tableName(table.getName())
                .tableOperation(Operation.DELETE)
                .conditions(Collections.singletonList(condition))
                .build();
    }

    public TableQuery update(String query, Metadata metadata) {
        Table table = QueryUtil.getTable(metadata, StringUtil.match(query, UPDATE, SET));
        Condition condition = getCondition(query);
        Map<String, String> fieldPair = getField(query, SET);
        Map.Entry<String, String> fieldEntry = fieldPair.entrySet().stream().findFirst().get();

        Field field = Field.builder()
                .column(QueryUtil.getColumn(table, fieldEntry.getKey()))
                .value(fieldEntry.getValue())
                .build();

        return TableQuery.builder()
                .fields(Collections.singletonList(field))
                .schemaName(metadata.getDatabaseName())
                .tableName(table.getName())
                .tableOperation(Operation.UPDATE)
                .conditions(Collections.singletonList(condition))
                .build();
    }

    public TableQuery insert(String query, Metadata metadata) {
        Table table = QueryUtil.getTable(metadata, StringUtil.matchTo(StringUtil.match(query, INTO, VALUES), "\\("));
        String[] columns = removeParenthesis(StringUtil.match(query, table.getName(), VALUES)).split(COMMA);
        String[] values = removeParenthesis(StringUtil.matchFrom(query, VALUES)).split(COMMA);

        List<Field> fields = new ArrayList<>();
        for (int i = 0; i < columns.length; i++) {
            fields.add(Field.builder()
                    .column(QueryUtil.getColumn(table, columns[i].trim()))
                    .value(values[i].trim())
                    .build());
        }

        Row row = Row.builder()
                .fields(fields)
                .build();

        return TableQuery.builder()
                .schemaName(metadata.getDatabaseName())
                .tableName(table.getName())
                .rows(Collections.singletonList(row))
                .tableOperation(Operation.INSERT)
                .build();
    }

    public TableQuery drop(String query, Metadata metadata) {
        Table table = QueryUtil.getTable(metadata, StringUtil.matchFrom(query, DROP_TABLE));

        return TableQuery.builder()
                .schemaName(metadata.getDatabaseName())
                .tableName(table.getName())
                .tableOperation(Operation.DROP)
                .build();
    }

    public TableQuery create(String query, Metadata metadata) {
        String table = StringUtil.match(query, CREATE_TABLE, "\\(");
        String[] columnStrings = removeParenthesis(StringUtil.matchFrom(query, table)).split(COMMA);

        List<Column> columns = new ArrayList<>();
        ForeignKey foreignKey = null;
        String primaryKey = null;
        Column primaryColumn = null;

        for (String column: columnStrings) {
            String[] details = column.trim().split("\\s");

            if (column.contains(PRIMARY_KEY)) {
                primaryKey = details[2];
            }

            if (column.contains(FOREIGN_KEY)) {
                String currentTableColumn = StringUtil.match(column, FOREIGN_KEY, REFERENCES);
                String[] strings = StringUtil.matchFrom(query, REFERENCES).split("\\(");
                foreignKey = ForeignKey.builder()
                        .foreignKeyColumn(currentTableColumn)
                        .referenceColumnName(removeParenthesis(strings[1]))
                        .referenceTableName(strings[0])
                        .build();
            }

            columns.add(Column.builder()
                    .name(details[0])
                    .dataType(details[1])
                    .build());
        }

        if (Objects.nonNull(primaryKey)) {
            String finalPrimaryColumn = primaryKey;
            primaryColumn = columns.stream()
                    .filter(data -> data.getName().equalsIgnoreCase(finalPrimaryColumn))
                    .findFirst()
                    .get();
        }

        return TableQuery.builder()
                .schemaName(metadata.getDatabaseName())
                .tableName(table)
                .columns(columns)
                .table(Table.builder()
                        .name(table)
                        .columns(columns)
                        .primaryKey(primaryColumn)
                        .foreignKeys(Objects.nonNull(foreignKey) ? Collections.singletonList(foreignKey) : null)
                        .build())
                .tableOperation(Operation.CREATE)
                .build();
    }

    private String removeParenthesis(String string) {
        return string.replaceAll("[()]", "");
    }

    private Map<String, String> getField(String string, String limiter) {
        String field;
        if (string.contains(WHERE)) {
            field = StringUtil.match(string, limiter, WHERE);
        } else {
            field = StringUtil.matchFrom(string, limiter);
        }

        String[] strings = field.split("\\s");
        String column = strings[0].trim();
        String value = strings[2].trim();

        return Collections.singletonMap(column, value);
    }

    private String getTable(String query, String limiter) {
        String table;
        if (query.contains(WHERE)) {
            table = StringUtil.match(query, limiter, WHERE);
        } else {
            table = StringUtil.matchFrom(query, limiter);
        }
        return table;
    }

    private Condition getCondition(String query) {
        Condition condition = null;
        if (query.contains(WHERE)) {
            Operator operator = getOperator(query);
            String column = StringUtil.match(query, WHERE, operator.operatorValue);
            String value = StringUtil.matchFrom(StringUtil.matchFrom(query, WHERE), operator.operatorValue);

            condition = Condition.builder()
                    .operand1(column)
                    .operand2(value)
                    .operator(operator)
                    .build();
        }
        return condition;
    }

    private List<Column> getColumns(String string, List<Column> allColumns) {
        if (ASTERISK.equals(string)) {
            return allColumns;
        }

        List<String> columnStrings = Arrays.asList(string.replaceAll("\\s","").split(","));
        return allColumns.stream()
                .filter(e -> columnStrings.contains(e.getName().toUpperCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    private Operator getOperator(String string) {
        Operator result = null;
        for (Operator operator: Operator.values()) {
            if (StringUtil.matchFrom(string, WHERE).contains(operator.operatorValue)) {
                result = operator;
            }
        }
        return result;
    }
}
