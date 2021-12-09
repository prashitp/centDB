package com.example.handler;

import com.example.models.Metadata;
import com.example.models.TableQuery;
import com.example.models.TransactionMessage;
import com.example.models.enums.Operation;
import com.example.services.QueueService;
import com.example.services.accessor.FileAccessorImpl;
import com.example.services.logs.GeneralLogService;
import com.example.services.logs.LogService;
import com.example.services.metadata.MetadataService;
import com.example.services.metadata.MetadataServiceImpl;
import com.example.services.parser.DatabaseParser;
import com.example.services.parser.TableParser;
import com.example.services.processor.TableProcessor;
import com.example.util.QueryUtil;
import lombok.SneakyThrows;

import java.sql.Timestamp;
import java.util.*;

public class InputTransaction {

    private static LogService logService;
    private static List<TransactionMessage> transactionMessages = new ArrayList<>();

    public static void query(Scanner scanner, Metadata metadata, String transactionId) {
        logService = GeneralLogService.getInstance();
        TRANSACTION: do {
            try {
                System.out.print("TRANSACTION> ");
                final String query = scanner.nextLine();

                operate(scanner, metadata, transactionId, query);

            } catch (Exception e) {
                e.printStackTrace();
                continue TRANSACTION;
            }
        } while (true);
    }

    public static void operate(Scanner scanner, Metadata metadata, String transactionId, String query) {

        QueryUtil.getOperation(query).accept(new Operation.OperationVisitor<Void>() {
            final TableParser tableParser = new TableParser();

            @Override
            public Void visitUse() {
                return null;
            }

            @Override
            public Void visitCreate() {
                TableQuery tableQuery = tableParser.create(query, metadata);
                addMessage(metadata, transactionId, query, tableQuery);
                return null;
            }

            @Override
            public Void visitDrop() {
                return null;
            }

            @Override
            public Void visitInsert() {
                TableQuery tableQuery = tableParser.insert(query, metadata);
                addMessage(metadata, transactionId, query, tableQuery);
                return null;
            }

            @Override
            @SneakyThrows
            public Void visitSelect() {
                TableQuery tableQuery = tableParser.select(query, metadata);
                addMessage(metadata, transactionId, query, tableQuery);
                return null;
            }

            @Override
            public Void visitUpdate() {
                TableQuery tableQuery = tableParser.update(query, metadata);
                addMessage(metadata, transactionId, query, tableQuery);
                return null;
            }

            @Override
            public Void visitDelete() {
                TableQuery tableQuery = tableParser.delete(query, metadata);
                addMessage(metadata, transactionId, query, tableQuery);
                return null;
            }

            @Override
            public Void visitCommit() {
                QueueService queueService = new QueueService();
                for (TransactionMessage message: transactionMessages) {
                    queueService.save(message);
                }
                queueService.commit(transactionId, metadata.getDatabaseName());
                InputOperation.query(scanner);
                return null;
            }

            @Override
            public Void visitStartTransaction() {
                return null;
            }
        });

    }

    private static void addMessage(Metadata metadata, String transactionId, String query, TableQuery tableQuery) {
        transactionMessages.add(TransactionMessage.builder()
                .id(transactionId)
                .database(metadata.getDatabase().getName())
                .query(query)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .tableQuery(tableQuery)
                .build());
    }
}
