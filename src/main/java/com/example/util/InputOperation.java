package com.example.util;

import com.example.models.enums.Operation;
import com.example.services.accessor.FileAccessorImpl;
import com.example.services.metadata.DatabaseMetadataServiceImpl;
import com.example.services.processor.TableProcessor;
import lombok.SneakyThrows;

import java.util.*;


public class InputOperation {

    public static void query(Scanner scanner) {
        System.out.print("SQL> \n");
        final String query = scanner.nextLine();

        List<String> strings = Arrays.asList(query.split(" "));
        Operation operation = Operation.valueOf(strings.get(0).trim().toUpperCase(Locale.ROOT));

        TableProcessor tableProcessor = new TableProcessor(new DatabaseMetadataServiceImpl(), new FileAccessorImpl());

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
                tableProcessor.select(query);
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
