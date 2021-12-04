package com.example.util;

import com.example.models.Column;
import com.example.models.enums.Operation;
import com.example.models.enums.Operator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
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

    public static void select(String query) {
        String table;
        String columnsString = StringUtil.match(query, SELECT, FROM);

        if (ASTERISK.equals(columnsString)) {
            List<Column> columns; // get columns from file.
        }

        if (query.contains(WHERE)) {
            table = StringUtil.match(query, FROM, WHERE);
            Operator operator = getOperator(query);
            String column = StringUtil.match(query, WHERE, operator.operatorValue);
        } else {
            table = StringUtil.match(query, FROM);
        }
        System.out.println(table);
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
