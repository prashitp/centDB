package com.example.services.parser;

import com.example.models.*;
import com.example.models.enums.Entity;
import com.example.models.enums.Operation;
import com.example.models.enums.Operator;
import com.example.services.metadata.MetadataService;
import com.example.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.util.Constants.*;
import static com.example.util.Constants.FROM;

@AllArgsConstructor
public class TableParser {

    private MetadataService metadataService;

    @SneakyThrows
    public TableQuery select(String query) {
        String database = "CENT_DB1";
        String table;
        Condition condition = null;
        List<Column> allColumns;
        Metadata metadata = metadataService.read(Entity.DATABASE, database);

        if (query.contains(WHERE)) {
            table = StringUtil.match(query, FROM, WHERE);
            Operator operator = getOperator(query);
            String column = StringUtil.match(query, WHERE, operator.operatorValue);

            condition = Condition.builder()
                    .operand1(table)
                    .operand2(column)
                    .operator(operator)
                    .build();
        } else {
            table = StringUtil.match(query, FROM);
        }

        allColumns = metadata.getAllColumnsForTable(table);

        return TableQuery.builder()
                .schemaName(database)
                .tableName(table)
                .columns(getColumns(StringUtil.match(query, SELECT, FROM), allColumns))
                .tableOperation(Operation.SELECT)
                .conditions(Collections.singletonList(condition))
                .build();

    }

    private List<Column> getColumns(String string, List<Column> allColumns) {
        if (ASTERISK.equals(string)) {
            return allColumns;
        }

        List<String> columnStrings = Arrays.asList(string.replaceAll("\\s","").split(","));
        return allColumns.stream()
                .filter(e -> columnStrings.contains(e.getName()))
                .collect(Collectors.toList());
    }

    private Operator getOperator(String string) {
        Operator result = null;
        for (Operator operator: Operator.values()) {
            if (string.contains(operator.operatorValue)) {
                result = operator;
            }
        }
        return result;
    }
}
