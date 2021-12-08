package com.example.handler;

import com.example.models.*;
import com.example.models.context.LogContext;
import com.example.models.enums.Operation;
import com.example.models.erd.Erd;
import com.example.services.logs.*;
import com.example.services.accessor.FileAccessorImpl;
import com.example.services.metadata.MetadataService;
import com.example.services.metadata.MetadataServiceImpl;
import com.example.services.parser.DatabaseParser;
import com.example.services.parser.TableParser;
import com.example.services.processor.TableProcessor;
import com.example.util.Constants;
import com.example.util.QueryUtil;
import lombok.SneakyThrows;

import java.util.*;


public class InputOperation {

    private static Metadata metadata;
    private static LogService generalLogService;
    private static LogService queryLogService;
    private static LogService eventLogService;

    public static void query(Scanner scanner) {
        generalLogService = GeneralLogService.getInstance();
        queryLogService = QueryLogService.getInstance();
        eventLogService = EventLogService.getInstance();

        QUERY: do {
            try {
                System.out.print("SQL> ");
                final String query = scanner.nextLine();

                LogContext.setQuery(query);
                long startTime = System.currentTimeMillis();

                operate(scanner, query.toUpperCase(Locale.ROOT));

                long endTime = System.currentTimeMillis();
                LogContext.setExecutionTime(endTime-startTime);
                generalLogService.log("Query execution successful");
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
                LogContext.setDatabaseState(Constants.DATABASE_STATE_ONLINE);
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
                List<Row> rows = tableProcessor.insert(tableQuery);

                LogContext.setTable(QueryUtil.getTable(metadata, tableQuery.getTableName()));
                LogContext.setDatabaseTables(metadata.getAllTablesWithRows());
                queryLogService.log(rows.size() + " rows inserted");
                return null;
            }

            @Override
            @SneakyThrows
            public Void visitSelect() {
                checkDatabase(metadata);
                TableQuery tableQuery = tableParser.select(query, metadata);
                List<Row> rows = tableProcessor.select(tableQuery);

                LogContext.setTable(QueryUtil.getTable(metadata, tableQuery.getTableName()));
                queryLogService.log( rows.size() + " rows returned");
                return null;
            }

            @Override
            public Void visitUpdate() {
                checkDatabase(metadata);
                TableQuery tableQuery = tableParser.update(query, metadata);
                List<Row> rows = tableProcessor.update(tableQuery);

                LogContext.setTable(QueryUtil.getTable(metadata, tableQuery.getTableName()));
                queryLogService.log(rows.size() + " rows updated");
                return null;
            }

            @Override
            public Void visitDelete() {
                checkDatabase(metadata);
                TableQuery tableQuery = tableParser.delete(query, metadata);
                List<Row> rows = tableProcessor.delete(tableQuery);

                LogContext.setTable(QueryUtil.getTable(metadata, tableQuery.getTableName()));
                LogContext.setDatabaseTables(metadata.getAllTablesWithRows());
                queryLogService.log(rows.size() + " rows deleted");
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
            LogContext.setDatabaseTables(metadata.getAllTablesWithRows());
        }
    }

    public static void generateERD(Scanner scanner) {
        System.out.println("Enter the database name : ");
        final String databaseName = scanner.nextLine();
        Erd erd = new Erd();
        erd.generate(databaseName);
    }
}
