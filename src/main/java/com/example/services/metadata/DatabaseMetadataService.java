package com.example.services.metadata;

import com.example.models.Column;
import com.example.models.Database;
import com.example.models.Metadata;
import com.example.models.Table;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseMetadataService implements IMetadataService {

    enum MetadataToken {
        DB, TB, CL, PK
    }

    private final static String METADATA_BASE_DIRECTORY = "dbmsMetadata/";
    private final static String METADATA_FILE_PREFIX = "MD_";
    private final static String METADATA_FILE_EXTENSION = ".txt";

    @Override
    public boolean writeMetadataToFile(Metadata metadata) {
        return false;
    }

    @Override
    public Metadata readMetadataForDatabase(String databaseName) {
        Metadata metadata = new Metadata();
        try {
            metadata = readMetaDataFile(databaseName);
            if (!validateMetadataEntries(metadata)) {
                throw new Exception("Invalid metadata object : " + metadata.toString());
            }
        } catch (Exception e) {
            System.out.println("Exception while constructing metadata for database :" + databaseName);
            System.out.println("Error message :" + e.getMessage());
        }
        return metadata;
    }

    private Metadata readMetaDataFile(String databaseName) throws Exception {
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

    private Metadata parseMetadataFile(List<String> lines) throws Exception {
        String databaseName = null;
        String tableName = null;
        String primaryKey = null;
        Metadata metadata = new Metadata();
        Database database = new Database();
        Map<String, String> columns = new HashMap<>();

        Table table = new Table();
        List<Column> dbColumns = new ArrayList<>();
        for (String line: lines) {
            StringTokenizer tokenizer = new StringTokenizer(line, "|");
            String token = tokenizer.hasMoreTokens() == true ? tokenizer.nextToken() : null;
            if (MetadataToken.DB.name().equals(token))  {
                databaseName = tokenizer.hasMoreTokens() == true ? tokenizer.nextToken() : null;
                database.setName(databaseName);
            }
            else if (MetadataToken.TB.name().equals(token)) {
                tableName = tokenizer.hasMoreTokens() == true ? tokenizer.nextToken() : null;
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
            }
            else if (MetadataToken.CL.name().equals(token)) {
                String columnName = tokenizer.hasMoreTokens() == true ? tokenizer.nextToken() : null;
                String dataType = tokenizer.hasMoreTokens() == true ? tokenizer.nextToken() : null;
                columns.put(columnName, dataType);
            }
            else if (MetadataToken.PK.name().equals(token)) {
                primaryKey = tokenizer.hasMoreTokens() == true ? tokenizer.nextToken() : null;
                table.setPrimaryKey(new Column(primaryKey, columns.get(primaryKey)));
            }
            else if (token == null) {
                throw new Exception("Malformed metadata file");
            }
        }
//        Add the last entry
        dbColumns = columns.keySet().stream().map(k -> new Column(k, columns.get(k))).collect(Collectors.toList());
        table.setColumns(dbColumns);

        metadata.setDatabase(database);

        return metadata;
    }

    private boolean validateMetadataEntries(Metadata metadata) {
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
