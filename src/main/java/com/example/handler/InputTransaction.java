package com.example.handler;

import com.example.models.Metadata;
import com.example.models.TableQuery;
import com.example.models.enums.Operation;
import com.example.services.accessor.FileAccessorImpl;
import com.example.services.metadata.MetadataService;
import com.example.services.metadata.MetadataServiceImpl;
import com.example.services.parser.DatabaseParser;
import com.example.services.parser.TableParser;
import com.example.services.processor.TableProcessor;
import lombok.SneakyThrows;

import java.util.*;

public class InputTransaction {

    private static List<TableQuery> tableQueries = new ArrayList<>();

    public static void query(Scanner scanner, Metadata metadata) {
        TRANSACTION: do {
            try {
                System.out.print("TRANSACTION> ");
                final String query = scanner.nextLine();

//                logService = new LogService();
                operate(scanner, metadata, query);
            } catch (Exception e) {
                continue TRANSACTION;
            }
        } while (true);
    }

    public static void operate(Scanner scanner, Metadata metadata, String query) {
        List<String> strings = Arrays.asList(query.split("\\s"));
        Operation operation = Operation.valueOf(strings.get(0).trim().toUpperCase(Locale.ROOT));

        operation.accept(new Operation.OperationVisitor<Void>() {

            final MetadataService metadataService = new MetadataServiceImpl();
            final DatabaseParser databaseParser = new DatabaseParser(metadataService);
            final TableProcessor tableProcessor = new TableProcessor(new FileAccessorImpl());
            final TableParser tableParser = new TableParser();

            @Override
            public Void visitUse() {
                return null;
            }

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
                TableQuery tableQuery = tableParser.insert(query, metadata);
                tableQueries.add(tableQuery);
                return null;
            }

            @Override
            @SneakyThrows
            public Void visitSelect() {
                TableQuery tableQuery = tableParser.select(query, metadata);
                tableQueries.add(tableQuery);
                return null;
            }

            @Override
            public Void visitUpdate() {
                TableQuery tableQuery = tableParser.update(query, metadata);
                tableQueries.add(tableQuery);
                return null;
            }

            @Override
            public Void visitDelete() {
                TableQuery tableQuery = tableParser.delete(query, metadata);
                tableQueries.add(tableQuery);
                return null;
            }

            @Override
            public Void visitCommit() {
                return null;
            }

            @Override
            public Void visitStartTransaction() {
                return null;
            }
        });

    }
}
