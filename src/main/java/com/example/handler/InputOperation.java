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
import java.util.stream.Collectors;

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

                operate(scanner, query);

                long endTime = System.currentTimeMillis();
                LogContext.setExecutionTime(endTime-startTime);
                generalLogService.log(String.format("Query successful - %s",query));
            } catch (Exception e) {
                eventLogService.log(String.format("Operation failed - %s",e.getMessage()));
                System.out.println("Error : " + e.getMessage());
                continue QUERY;
            }
        } while (true);
    }

    public static void operate(Scanner scanner, String query) {
        QueryUtil.getOperation(query).accept(new Operation.OperationVisitor<Void>() {

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

                        System.out.println("Database created");
                        eventLogService.log("Database created");
                        return null;
                    }

                    @Override
                    public Void visitTable() {
                        TableQuery tableQuery = tableParser.create(query, metadata);
                        tableProcessor.create(tableQuery);

                        System.out.println("Table created");
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

                System.out.println("Table dropped");
                eventLogService.log("Table dropped");
                return null;
            }

            @Override
            public Void visitInsert() {
                checkDatabase(metadata);
                TableQuery tableQuery = tableParser.insert(query, metadata);

                queryLogService.log(String.format("Insert started - %s",query));
                List<Row> rows = tableProcessor.insert(tableQuery);
                System.out.printf("%d rows inserted \n", rows.size());

                LogContext.setTable(QueryUtil.getTable(metadata, tableQuery.getTableName()));
                getAllTablesWithRows(metadata);
                queryLogService.log(rows.size() + " rows inserted");
                return null;
            }

            @Override
            @SneakyThrows
            public Void visitSelect() {
                checkDatabase(metadata);

                queryLogService.log(String.format("Selection started - %s",query));
                TableQuery tableQuery = tableParser.select(query, metadata);
                List<Row> rows = tableProcessor.select(tableQuery);

                printRow(rows);

                LogContext.setTable(QueryUtil.getTable(metadata, tableQuery.getTableName()));
                queryLogService.log( rows.size() + " rows selected");
                return null;
            }

            @Override
            public Void visitUpdate() {
                checkDatabase(metadata);
                TableQuery tableQuery = tableParser.update(query, metadata);
                List<Row> rows = tableProcessor.update(tableQuery);
                System.out.printf("%d rows updated \n", rows.size());

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
                System.out.printf("%d rows deleted \n", rows.size());

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
                InputTransaction.query(scanner, metadata, UUID.randomUUID().toString());
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
        ExportService.createErd(databaseName);
        generalLogService.log("ERD created");
    }

    public static void exportData(Scanner scanner) {
        System.out.println("Enter the database name to be exported: ");
        final String databaseName = scanner.nextLine();
        ExportService.createSqlDump(databaseName);
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

    public static void printRow(List<Row> rowList){
        boolean printHeader = true;
        for (Row row: rowList){
            List<Field> fields = row.getAllFieldsOfRow();

            if (printHeader) {
                List<String> columnNames = fields.stream().map(field -> field.getColumn().getName()).collect(Collectors.toList());
                StringBuilder builder = new StringBuilder();
                for (String columnName : columnNames) {
                    builder.append(padLeftWithSpaces(columnName, 30));
                }
                System.out.format("------------------------------------------------------------------------------------------\n");
                System.out.println(builder.toString());
                System.out.format("------------------------------------------------------------------------------------------\n");
                printHeader = false;
            }

            StringBuilder rowValue = new StringBuilder();
            for (Field field: fields){
               rowValue.append(padLeftWithSpaces(field.getValue().toString(), 30));
            }
            System.out.println(rowValue.toString());
            System.out.format("------------------------------------------------------------------------------------------\n");
        }
    }

    private static String padLeftWithSpaces(String str, int length) {
        if (str.length() > length) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        int currLen = sb.length();
        while (currLen <= length) {
            sb.append(" ");
            currLen++;
        }
        return sb.toString();
    }

}
