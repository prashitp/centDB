package com.example.util;

import com.example.models.*;
import com.example.models.enums.Entity;
import com.example.models.enums.Operation;
import com.example.models.enums.Operator;
import com.example.services.accessor.FileDataAccessorImpl;
import com.example.services.accessor.TableDataAccessor;
import com.example.services.metadata.DatabaseMetadataServiceImpl;
import lombok.SneakyThrows;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.util.Constants.*;

public class InputOperation {

    public static void query(Scanner scanner) {
        System.out.print("SQL> \n");
        final String query = scanner.nextLine();

        List<String> strings = Arrays.asList(query.split(" "));

        Operation operation = Operation.valueOf(strings.get(0).trim().toUpperCase(Locale.ROOT));

        operation.accept(new Operation.OperationVisitor<Void>() {
            @Override
            public Void visitCreate() {
                return null;
            }

            @Override
            public Void visitDrop() {
                return null;
            }

            @Override
            public Void visitInsert() {
                return null;
            }

            @Override
            @SneakyThrows
            public Void visitSelect() {
                TableQuery tableQuery = select(query);
                TableDataAccessor tableDataAccessor = new FileDataAccessorImpl();
                List<Row> rows = tableDataAccessor.read(tableQuery);
                return null;
            }

            @Override
            public Void visitUpdate() {
                return null;
            }

            @Override
            public Void visitDelete() {
                return null;
            }

            @Override
            public Void visitAlter() {
                return null;
            }

            @Override
            public Void visitTruncate() {
                return null;
            }
        });
    }

    public static TableQuery select(String query) {
        String database = "";
        String table;
        Condition condition = null;

        Metadata metadata = new DatabaseMetadataServiceImpl().read(Entity.DATABASE, database);
        List<Column> allColumns = metadata.getAllColumnsForTable(database);

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

        return TableQuery.builder()
                .schemaName(database)
                .tableName(table)
                .columns(getColumns(StringUtil.match(query, SELECT, FROM), allColumns))
                .tableOperation(Operation.SELECT)
                .conditions(Collections.singletonList(condition))
                .build();
    }

    private static List<Column> getColumns(String string, List<Column> allColumns) {
        if (ASTERISK.equals(string)) {
            return allColumns;
        }

        List<String> columnStrings = Arrays.asList(string.replaceAll("\\s","").split(","));
        return allColumns.stream()
                .filter(e -> columnStrings.contains(e.getName()))
                .collect(Collectors.toList());
    }

    private static Operator getOperator(String string) {
        Operator result = null;
        for (Operator operator: Operator.values()) {
            if (string.contains(operator.operatorValue)) {
                result = operator;
            }
        }
        return result;
    }



    private static void create(List<String> strings) {

    }

    private static void drop(List<String> strings) {

    }

    private static void delete(List<String> strings) {

    }

    private static void alter(List<String> strings) {

    }

    private static void truncate(List<String> strings) {

    }
}
