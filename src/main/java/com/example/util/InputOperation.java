package com.example.util;

import com.example.models.Column;
import com.example.models.Condition;
import com.example.models.TableQuery;
import com.example.models.enums.Operation;
import com.example.models.enums.Operator;

import java.util.*;

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
            public Void visitSelect() {
                select(query);
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
        String table = StringUtil.match(query, FROM);
        String columnMatch = StringUtil.match(query, SELECT, FROM);
        List<String> columns = new ArrayList<>();
        Condition condition = null;

        if (ASTERISK.equals(columnMatch)) {
            // get all columns
        } else {
            // get specified columns
        }

        if (query.contains(WHERE)) {
            table = StringUtil.match(query, FROM, WHERE);
            Operator operator = getOperator(query);
            String column = StringUtil.match(query, WHERE, operator.operatorValue);

            condition = Condition.builder()
                    .operand1(table)
                    .operand2(column)
                    .operator(operator)
                    .build();
        }

        return TableQuery.builder()
                .schemaName(database)
                .tableName(table)
                .columns(new ArrayList<>())
                .tableOperation(Operation.SELECT)
                .conditions(Collections.singletonList(condition))
                .build();
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
