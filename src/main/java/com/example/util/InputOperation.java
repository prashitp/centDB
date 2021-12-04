package com.example.util;

import com.example.models.enums.Operation;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

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
                select(strings);
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

    private static void select(List<String> strings) {

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
