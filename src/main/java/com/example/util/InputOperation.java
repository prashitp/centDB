package com.example.util;

import com.example.models.enums.Operation;

import java.util.Scanner;

public class InputOperation {

    public static void query(Scanner scanner) {
        System.out.print("SQL> \n");
        final String query = scanner.nextLine();

        String[] tokens = query.split(" ");

        Operation operation = Operation.valueOf(tokens[0]);

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
}
