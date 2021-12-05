package com.example.handler;

import com.example.models.Row;
import com.example.models.TableQuery;
import com.example.models.enums.Operation;
import com.example.services.accessor.FileAccessorImpl;
import com.example.services.metadata.DatabaseMetadataServiceImpl;
import com.example.services.parser.TableParser;
import com.example.services.processor.TableProcessor;
import lombok.SneakyThrows;

import java.util.*;


public class InputOperation {

    public static void query(Scanner scanner) {
        System.out.print("SQL> \n");
        final String query = scanner.nextLine();

        List<String> strings = Arrays.asList(query.split("\\s"));
        Operation operation = Operation.valueOf(strings.get(0).trim().toUpperCase(Locale.ROOT));

        operation.accept(new Operation.OperationVisitor<Void>() {
            final TableProcessor tableProcessor = new TableProcessor(new FileAccessorImpl());
            final TableParser tableParser = new TableParser(new DatabaseMetadataServiceImpl());

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
                TableQuery tableQuery = tableParser.select(query);
                List<Row> rows = tableProcessor.select(tableQuery);
                // Logger Logic
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
