package com.example.handler;

import com.example.models.*;
import com.example.models.context.LogContext;
import com.example.models.enums.Operation;
import com.example.services.LogService;
import com.example.services.accessor.FileAccessorImpl;
import com.example.services.metadata.MetadataService;
import com.example.services.metadata.MetadataServiceImpl;
import com.example.services.parser.DatabaseParser;
import com.example.services.parser.TableParser;
import com.example.services.processor.TableProcessor;
import lombok.SneakyThrows;

import java.util.*;


public class InputOperation {

    private static Metadata metadata;
    private static LogService logService;

    public static void query(Scanner scanner) {
        QUERY: do {
            try {
                System.out.print("SQL> ");
                final String query = scanner.nextLine();

                logService = new LogService();
                operate(scanner, query.toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                continue QUERY;
            }
        } while (true);
    }

    public static void operate(Scanner scanner, String query) {
        List<String> strings = Arrays.asList(query.split("\\s"));
        Operation operation = Operation.valueOf(strings.get(0).trim().toUpperCase(Locale.ROOT));

        operation.accept(new Operation.OperationVisitor<Void>() {

            final MetadataService metadataService = new MetadataServiceImpl();
            final DatabaseParser databaseParser = new DatabaseParser(metadataService);
            final TableProcessor tableProcessor = new TableProcessor(new FileAccessorImpl());
            final TableParser tableParser = new TableParser();

            @Override
            public Void visitUse() {
                metadata = databaseParser.use(query);
                checkDatabase(metadata);
                System.out.printf("%s selected \n", metadata.getDatabaseName());
                logService.log("Database Selected");
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
                checkDatabase(metadata);
                TableQuery tableQuery = tableParser.insert(query, metadata);
                tableProcessor.insert(tableQuery);
                return null;
            }

            @Override
            @SneakyThrows
            public Void visitSelect() {
                checkDatabase(metadata);
                logService.log("Selection started");
                TableQuery tableQuery = tableParser.select(query, metadata);
                List<Row> rows = tableProcessor.select(tableQuery);

                LogContext.setTable(getTable(metadata, tableQuery.getTableName()));
                logService.log("Selection completed");
                // Logger Logic
                return null;
            }

            @Override
            public Void visitUpdate() {
                checkDatabase(metadata);
                TableQuery tableQuery = tableParser.update(query, metadata);
                tableProcessor.update(tableQuery);
                return null;
            }

            @Override
            public Void visitDelete() {
                checkDatabase(metadata);
                TableQuery tableQuery = tableParser.delete(query, metadata);
                tableProcessor.delete(tableQuery);
                return null;
            }

            @Override
            public Void visitAlter() {
                return null;
            }

            @Override
            public Void visitStartTransaction() {
                return null;
            }
        });
    }

    private static void checkDatabase(Metadata metadata) {
        if (Objects.isNull(metadata)) {
            System.out.print("Please select database \n");
        } else {
            LogContext.setMetadata(metadata);
        }
    }

    private static Table getTable(Metadata metadata, String tableName) {
        return metadata.getAllTablesFromDatabase()
                .stream()
                .filter(e -> e.getName().equalsIgnoreCase(tableName))
                .findFirst()
                .get();
    }
}
