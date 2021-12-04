package com.example.services.metadata;

import com.example.models.Column;
import com.example.models.Database;
import com.example.models.Metadata;
import com.example.models.Table;
import com.example.models.enums.Entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseMetadataServiceImpl implements MetadataService {

    enum MetadataToken {
        DB, TB, CL, PK
    }

    private final static String METADATA_BASE_DIRECTORY = "METADATA/";
    private final static String METADATA_FILE_PREFIX = "MD_";
    private final static String METADATA_FILE_EXTENSION = ".txt";

    @Override
    public boolean write(Entity entity, Metadata metadata) {
        return false;
    }

    @Override
    public boolean delete(Entity entity, Metadata metadata) {
        return false;
    }

    @Override
    public Metadata read(Entity entity, String databaseName) {
        Metadata metadata = new Metadata();
        try {
            metadata = readMetaDataFile(databaseName);
            if (!validate(metadata)) {
                throw new Exception("Invalid metadata object : " + metadata.toString());
            }
        } catch (Exception e) {
            System.out.println("Exception while constructing metadata for database :" + databaseName);
            System.out.println("Error message :" + e.getMessage());
        }
        return metadata;
    }

    private Metadata readMetaDataFile(String databaseName) {
        Metadata metadata = new Metadata();
        String metadataFileSuffix = databaseName.toUpperCase(Locale.ROOT);
        String metadataFilePathString = METADATA_BASE_DIRECTORY + METADATA_FILE_PREFIX + metadataFileSuffix + METADATA_FILE_EXTENSION;
        System.out.println("Reading metadata from " + metadataFilePathString);
        try {
            Path metadataFilePath = Paths.get(metadataFilePathString);
            List<String> lines = Files.lines(metadataFilePath).collect(Collectors.toList());
            metadata = parseMetadataFile(lines);
        }
        catch (InvalidPathException invalidPathException) {
            System.out.println("Cannot convert string to path");
            invalidPathException.printStackTrace();
        }
        catch (IOException ioException) {
            System.out.println("Exception while opening the metadata file");
            ioException.printStackTrace();
        }
        return metadata;
    }

    private Metadata parseMetadataFile(List<String> lines) {
        String databaseName;
        String tableName;
        String primaryKey;

        Metadata metadata = new Metadata();
        Database database = new Database();
        Table table = new Table();

        Map<String, String> columns = new HashMap<>();
        List<Column> dbColumns;

        for (String line: lines) {
            StringTokenizer tokenizer = new StringTokenizer(line, "|");
            String token = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;

            switch(MetadataToken.valueOf(token)) {
                case DB:
                    databaseName = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
                    database.setName(databaseName);
                    break;

                case TB:
                    tableName = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
                    if (columns.isEmpty()) {
                        table.setName(tableName);
                        database.addTable(table);
                        continue;
                    }
                    dbColumns = columns.keySet().stream().map(k -> new Column(k, columns.get(k))).collect(Collectors.toList());
                    table.setColumns(dbColumns);

//              initialize a new table and column object here and clean previous objects
                    columns.clear();
                    dbColumns = new ArrayList<>();
                    table = new Table();

                    table.setName(tableName);
                    database.addTable(table);
                    break;

                case PK:
                    primaryKey = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
                    table.setPrimaryKey(new Column(primaryKey, columns.get(primaryKey)));
                    break;

                case CL:
                    String columnName = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
                    String dataType = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
                    columns.put(columnName, dataType);
                    break;
                default:
                    throw new IllegalArgumentException("Malformed metadata file");
            }
        }

        dbColumns = columns.keySet().stream().map(k -> new Column(k, columns.get(k))).collect(Collectors.toList());
        table.setColumns(dbColumns);

        metadata.setDatabase(database);

        return metadata;
    }

    private boolean validate(Metadata metadata) {
        boolean isValid = true;
        if (Objects.isNull(metadata.getDatabase())) {
            isValid = false;
        }
        else {
            List<Table> tables = metadata.getAllTablesFromDatabase();
            if (Objects.isNull(tables) || tables.isEmpty()) {
                isValid = false;
            }
            else {
                for (Table table : tables) {
                    List<Column> columns = table.getColumns();
                    for (Column column : columns) {
                        if (Objects.isNull(column.getName()) || Objects.isNull(column.getDataType())) {
                            isValid = false;
                        }
                    }
                }
            }
        }
        return isValid;
    }

}
