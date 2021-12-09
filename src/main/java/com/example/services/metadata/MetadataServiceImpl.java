package com.example.services.metadata;

import com.example.models.*;
import com.example.models.enums.Entity;
import com.example.models.enums.MetadataToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class MetadataServiceImpl extends AbstractMetadataService {

    @Override
    public void write(Entity entity, Metadata metadata) throws Exception {
        switch (entity) {
            case DATABASE:
                createDatabase(metadata.getDatabase());
                break;

            case TABLE:
                List<Table> tables = metadata.getAllTablesFromDatabase();
                Table table = Objects.nonNull(tables) ? tables.get(0) : null;
                createTable(metadata.getDatabase(), table);
                break;

            default:
                throw new Exception("Invalid entity");
        }
    }

    private boolean createDatabase(Database database) throws Exception {
        database.setName(database.getName().toUpperCase(Locale.ROOT));
        return createDirectory(database) && createMetadataFile(database) && addMetadataEntry(database);
    }

    private boolean createDirectory(Database database) {
        String dirPath = getDirectoryPath(database);
        File dbDir = new File(dirPath);
        return dbDir.mkdir();
    }

    private boolean createMetadataFile(Database database) throws Exception {
        String dbName = database.getName();
        boolean success;
        String filePath = getMetadataFilePath(database.getName());
        File file = new File(filePath);
        try {
            success = file.createNewFile();
        } catch (IOException ioException) {
            throw new Exception("Error while creating database " + dbName);
        }
        return success;
    }

    private boolean addMetadataEntry(Database database) throws Exception {
        String file = getMetadataFilePath(database.getName());
        Path filePath = Paths.get(file);
        String entry = entryBuilder(database);
        return appendToFile(filePath, Arrays.asList(entry));
    }

    private boolean createTable(Database database, Table table) throws Exception {
        boolean success;
        database.setName(database.getName().toUpperCase(Locale.ROOT));
        table.setName(table.getName().toUpperCase(Locale.ROOT));
        if (table.getColumns().size() < 1) {
            throw new Exception("No columns present for table " + table.getName());
        }
        success = createFile(database, table) && addMetadataEntry(database, table);
        return success;
    }

    private boolean createFile(Database database, Table table) throws Exception {
        String dbName = database.getName();
        String tableName = table.getName();
        boolean success;
        String filePath = getTableDataFilePath(dbName, tableName);
        File file = new File(filePath);
        try {
            success = file.createNewFile();
        } catch (IOException ioException) {
            throw new Exception("Error while creating table " + tableName);
        }
        return success;
    }

    private boolean addMetadataEntry(Database database,Table table) throws Exception {
        String dbName = database.getName();
        String metadataFile = getMetadataFilePath(dbName);
        Path metadataFilePath = Paths.get(metadataFile);
        List<String> entries = new ArrayList<>();
        entries.add(entryBuilder(table));
        table.getColumns().forEach(column -> entries.add(entryBuilder(column)));
        Optional.ofNullable(table.getPrimaryKey()).ifPresent(column -> entries.add(entryBuilderPrimaryKey(table.getPrimaryKey())));
        if (!table.getForeignKeys().isEmpty()) {
            Optional.ofNullable(table.getForeignKeys()).ifPresent(foreignKeys ->
                    foreignKeys.forEach(foreignKey -> entries.add(entryBuilder(foreignKey))));
        }
        return appendToFile(metadataFilePath, entries);
    }

    private boolean appendToFile(Path path, List<String> lines) throws Exception {
        boolean success = false;
        try {
            Files.write(path, lines, StandardOpenOption.APPEND, StandardOpenOption.APPEND);
            success = true;
        } catch (IOException ioException) {
            throw new Exception("Error creating table");
        }
        return success;
    }

    @Override
    public void delete(Entity entity, Metadata metadata) throws Exception {
        switch (entity) {
            case DATABASE:
                deleteDatabase(metadata.getDatabase());
                break;

            case TABLE:
                List<Table> tables = metadata.getAllTablesFromDatabase();
                Table table = Objects.nonNull(tables) ? tables.get(0) : null;
                deleteTable(metadata.getDatabase(), table);
                break;

            default:
                throw new Exception("Invalid delete operation");
        }
    }

    private void deleteDatabase(Database database) throws Exception {
        database.setName(database.getName().toUpperCase(Locale.ROOT));
        deleteDirectory(database);
        deleteMetadata(database);
    }

    private void deleteDirectory(Database database) throws Exception {
        String dirPath = getDirectoryPath(database);
        Path dbDirectory = Paths.get(dirPath);
        try {
            Files.walk(dbDirectory).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException ioException) {
            throw new Exception("Error deleting database " + database.getName());
        }
    }

    private void deleteMetadata(Database database) throws Exception {
        String metadataFile = getMetadataFilePath(database.getName());
        Path metadataFilePath = Paths.get(metadataFile);
        try {
            Files.delete(metadataFilePath);
        } catch (IOException ioException) {
            throw new Exception("Error deleting database " + database.getName());
        }
    }

    private void deleteTable(Database database, Table table) throws Exception {
        deleteFile(database, table);
        deleteMetadata(database, table);
    }

    private void deleteFile(Database database, Table table) throws Exception {
        String tableFile = getTableDataFilePath(database.getName(), table.getName());
        Path tableFilePath = Paths.get(tableFile);
        try {
            Files.delete(tableFilePath);
        } catch (IOException ioException) {
            throw new Exception("Error deleting table " + table.getName());
        }
    }

    private void deleteMetadata(Database database, Table table) throws Exception {
        String metadataFile = getMetadataFilePath(database.getName());
        Path metadataPath = Paths.get(metadataFile);
        List<String> allEntries = new ArrayList<>();
        try {
            allEntries = Files.lines(metadataPath).collect(Collectors.toList());
        } catch (IOException ioException) {
            throw new Exception("Error deleting table " + table.getName());
        }
        List<String> tableEntries = new ArrayList<>();
        tableEntries.add(entryBuilder(table));
        table.getColumns().forEach(column -> tableEntries.add(entryBuilder(column)));
        Optional.ofNullable(table.getPrimaryKey()).ifPresent(column -> tableEntries.add(entryBuilderPrimaryKey(column)));
        if (!table.getForeignKeys().isEmpty()) {
            Optional.ofNullable(table.getForeignKeys()).ifPresent(foreignKeys ->
                    foreignKeys.forEach(foreignKey -> tableEntries.add(entryBuilder(foreignKey))));
        }
        for (int i=0, j=0; i < allEntries.size() && j < tableEntries.size(); i++) {
            String allEntry = allEntries.get(i);
            String tableEntry = tableEntries.get(j);
            if (tableEntry.equalsIgnoreCase(allEntry)) {
                allEntries.remove(i);
                j++;
                i--;
            }
        }
        Files.write(metadataPath, allEntries);
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
        String metadataFile = getMetadataFilePath(databaseName);
        try {
            Path metadataFilePath = Paths.get(metadataFile);
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

        Map<String, String> columns = new LinkedHashMap<>();
        List<Column> dbColumns;

        for (String line: lines) {
            if (Objects.isNull(line) || (line.isEmpty())) {
                continue;
            }
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

                case FK:
                    String foreignKeyColumn = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
                    String referenceTableName = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
                    String referenceColumnName = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
                    ForeignKey foreignKey = new ForeignKey(foreignKeyColumn, referenceTableName, referenceColumnName);
                    table.addForeignKey(foreignKey);
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
