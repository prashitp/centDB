package com.example.util;

import com.example.models.Column;
import com.example.models.Metadata;
import com.example.models.Table;
import com.example.models.TableQuery;
import com.example.models.enums.Entity;
import com.example.models.enums.Operation;
import com.example.services.accessor.FileAccessorImpl;
import com.example.services.metadata.MetadataService;
import com.example.services.metadata.MetadataServiceImpl;
import com.example.services.parser.TableParser;
import com.example.services.processor.TableProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class QueryUtil {

    public static Table getTable(Metadata metadata, String tableName) {
        return metadata.getAllTablesFromDatabase()
                .stream()
                .filter(data -> data.getName().equalsIgnoreCase(tableName))
                .findFirst()
                .get();
    }

    public static Column getColumn(Table table, String columnName) {
        return table.getColumns()
                .stream()
                .filter(data -> data.getName().equalsIgnoreCase(columnName))
                .findFirst()
                .get();
    }

    public static TableQuery getQuery(String query, String database) {
        final TableParser tableParser = new TableParser();
        final MetadataService metadataService = new MetadataServiceImpl();

        Metadata metadata = metadataService.read(Entity.DATABASE, database);

        return getOperation(query).accept(new Operation.OperationVisitor<TableQuery>() {
            @Override
            public TableQuery visitUse() {
                return null;
            }

            @Override
            public TableQuery visitCreate() {
                return tableParser.create(query, metadata);
            }

            @Override
            public TableQuery visitDrop() {
                return tableParser.drop(query, metadata);
            }

            @Override
            public TableQuery visitInsert() {
                return tableParser.insert(query, metadata);
            }

            @Override
            public TableQuery visitSelect() {
                return tableParser.select(query, metadata);
            }

            @Override
            public TableQuery visitUpdate() {
                return tableParser.update(query, metadata);
            }

            @Override
            public TableQuery visitDelete() {
                return tableParser.delete(query, metadata);
            }

            @Override
            public TableQuery visitCommit() {
                return null;
            }

            @Override
            public TableQuery visitStartTransaction() {
                return null;
            }
        });
    }

    public static void execute(String query, String database) {
        final TableProcessor tableProcessor = new TableProcessor(new FileAccessorImpl());
        getOperation(query).accept(new Operation.OperationVisitor<Void>() {
            @Override
            public Void visitUse() {
                return null;
            }

            @Override
            public Void visitCreate() {
                tableProcessor.create(getQuery(query, database));
                return null;
            }

            @Override
            public Void visitDrop() {
                tableProcessor.drop(getQuery(query, database));
                return null;
            }

            @Override
            public Void visitInsert() {
                tableProcessor.insert(getQuery(query, database));
                return null;
            }

            @Override
            public Void visitSelect() {
                tableProcessor.select(getQuery(query, database));
                return null;
            }

            @Override
            public Void visitUpdate() {
                tableProcessor.update(getQuery(query, database));
                return null;
            }

            @Override
            public Void visitDelete() {
                tableProcessor.delete(getQuery(query, database));
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

    public static Operation getOperation(String query) {
        List<String> strings = Arrays.asList(query.split("\\s"));
        return Operation.valueOf(strings.get(0).trim().toUpperCase(Locale.ROOT));
    }
}
