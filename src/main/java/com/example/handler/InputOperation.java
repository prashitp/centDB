package com.example.handler;

import com.example.models.*;
import com.example.models.context.LogContext;
import com.example.models.enums.Entity;
import com.example.models.enums.Operation;
import com.example.services.ExportService;
import com.example.services.logs.*;
import com.example.services.accessor.FileAccessorImpl;
import com.example.services.metadata.MetadataService;
import com.example.services.metadata.MetadataServiceImpl;
import com.example.services.parser.DatabaseParser;
import com.example.services.parser.TableParser;
import com.example.services.processor.TableProcessor;
import com.example.util.QueryUtil;
import lombok.SneakyThrows;

import java.util.*;

import static com.example.util.Constants.CREATE_DATABASE;


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

                long startTime = System.currentTimeMillis();

                operate(scanner, query.toUpperCase(Locale.ROOT));

                long endTime = System.currentTimeMillis();
                LogContext.setExecutionTime(endTime-startTime);
                generalLogService.log(String.format("Query successful - %s",query));
            } catch (Exception e) {
                eventLogService.log(String.format("Operation failed - %s",e.getMessage()));
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
                return null;
            }

            @Override
            public Void visitCreate() {
                Entity entity = query.contains(CREATE_DATABASE) ? Entity.DATABASE : Entity.TABLE;
                entity.accept(new Entity.EntityVisitor<Void>() {
                    @Override
                    public Void visitDatabase() {
                        databaseParser.create(query);
                        eventLogService.log("Database created");
                        return null;
                    }

                    @Override
                    public Void visitTable() {
                        TableQuery tableQuery = tableParser.create(query, metadata);
                        tableProcessor.create(tableQuery);
                        eventLogService.log("Table created");
                        return null;
                    }
                });
                return null;
            }

            @Override
            public Void visitDrop() {
                checkDatabase(metadata);
                TableQuery tableQuery = tableParser.drop(query, metadata);
                tableProcessor.drop(tableQuery);
                eventLogService.log("Table dropped");
                return null;
            }

            @Override
            public Void visitInsert() {
                checkDatabase(metadata);
                TableQuery tableQuery = tableParser.insert(query, metadata);

                queryLogService.log(String.format("Insert started - %s",query));
                List<Row> rows = tableProcessor.insert(tableQuery);

                LogContext.setTable(QueryUtil.getTable(metadata, tableQuery.getTableName()));
//                LogContext.setDatabaseTables(getAllTablesWithRows(metadata));
                getAllTablesWithRows(metadata);
                queryLogService.log(rows.size() + " rows inserted");
                return null;
            }

            @Override
            @SneakyThrows
            public Void visitSelect() {
                checkDatabase(metadata);

                TableQuery tableQuery = tableParser.select(query, metadata);
                LogContext.setTable(QueryUtil.getTable(metadata, tableQuery.getTableName()));
                queryLogService.log(String.format("Selection started - %s",query));

                List<Row> rows = tableProcessor.select(tableQuery);

                queryLogService.log( rows.size() + " rows selected");
                return null;
            }

            @Override
            public Void visitUpdate() {
                checkDatabase(metadata);
                TableQuery tableQuery = tableParser.update(query, metadata);
                List<Row> rows = tableProcessor.update(tableQuery);

                queryLogService.log(String.format("Update started - %s",query));
                LogContext.setTable(QueryUtil.getTable(metadata, tableQuery.getTableName()));
                queryLogService.log(rows.size() + " rows updated");
                return null;
            }

            @Override
            public Void visitDelete() {
                checkDatabase(metadata);
                TableQuery tableQuery = tableParser.delete(query, metadata);

                queryLogService.log(String.format("Delete started - %s",query));
                List<Row> rows = tableProcessor.delete(tableQuery);

                LogContext.setTable(QueryUtil.getTable(metadata, tableQuery.getTableName()));
                getAllTablesWithRows(metadata);
                queryLogService.log(rows.size() + " rows deleted");
                return null;
            }

            @Override
            public Void visitCommit() {
                eventLogService.log("Transaction committed");
                return null;
            }

            @Override
            public Void visitStartTransaction() {
                checkDatabase(metadata);
                eventLogService.log("Transaction started");
                InputTransaction.query(scanner, metadata);
                eventLogService.log("Transaction completed");
                return null;
            }
        });
    }

    private static void checkDatabase(Metadata metadata) {
        if (Objects.isNull(metadata)) {
            System.out.print("Please select database \n");
        } else {
            LogContext.setMetadata(metadata);
            getAllTablesWithRows(metadata);
        }
    }

    public static void generateERD(Scanner scanner) {
        System.out.println("Enter the database name : ");
        final String databaseName = scanner.nextLine();

        long startTime = System.currentTimeMillis();
        generalLogService.log("Creating ERD");

        ExportService.createErd(databaseName);

        long endTime = System.currentTimeMillis();
        LogContext.setExecutionTime(endTime-startTime);
        generalLogService.log("ERD created");
    }

    private static void getAllTablesWithRows(Metadata metadata) {
        FileAccessorImpl accessor = new FileAccessorImpl();
        List<Table> tables = new ArrayList<>();
        metadata.getAllTablesFromDatabase().forEach(table -> {
            TableQuery query = TableQuery.builder().schemaName(metadata.getDatabaseName()).tableName(table.getName())
                    .columns(table.getColumns()).tableOperation(Operation.SELECT).build();
            try {
                List<Row> output = accessor.read(query);
                table.setRows(output);
            } catch (Exception e) {
                System.out.println("Error while reading file");
            }
            tables.add(table);
        });
        metadata.getDatabase().updateTables(tables);
    }
}
