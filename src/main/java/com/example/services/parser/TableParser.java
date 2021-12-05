package com.example.services.parser;

import com.example.models.*;
import com.example.models.enums.Operation;
import com.example.models.enums.Operator;
import com.example.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.example.util.Constants.*;
import static com.example.util.Constants.FROM;

@AllArgsConstructor
public class TableParser {

    public TableQuery select(String query, Metadata metadata) {
        String table = getTable(query, FROM);
        Condition condition = getCondition(query);
        List<Column> columns = getColumns(StringUtil.match(query, SELECT, FROM), metadata.getAllColumnsForTable(table));

        return TableQuery.builder()
                .schemaName(metadata.getDatabaseName())
                .tableName(table)
                .columns(columns)
                .tableOperation(Operation.SELECT)
                .conditions(Collections.singletonList(condition))
                .build();
    }

    public TableQuery delete(String query, Metadata metadata) {
        String table = getTable(query, FROM);
        Condition condition = getCondition(query);

        return TableQuery.builder()
                .schemaName(metadata.getDatabaseName())
                .tableName(table)
                .tableOperation(Operation.DELETE)
                .conditions(Collections.singletonList(condition))
                .build();
    }

    public TableQuery update(String query, Metadata metadata) {
        String table = getTable(query, SET);
        Condition condition = getCondition(query);

        return TableQuery.builder()
                .schemaName(metadata.getDatabaseName())
                .tableName(table)
                .tableOperation(Operation.UPDATE)
                .conditions(Collections.singletonList(condition))
                .build();
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
            String value = StringUtil.matchFrom(query, operator.operatorValue);

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
