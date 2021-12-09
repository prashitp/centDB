package com.example.services.accessor;

import com.example.exceptions.InvalidOperation;
import com.example.models.*;
import com.example.models.enums.Entity;

import com.example.models.enums.Operator;
import com.example.services.metadata.MetadataService;
import com.example.services.metadata.MetadataServiceImpl;
import com.example.models.enums.Operation;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class FileAccessorImpl implements TableAccessor {

    final static char PIPE_DELIMITER = '|';
    final static char ESCAPE_CHAR = '\\';

    final static String TABLE_DATA_FILE_PREFIX = "TB_";
    final static String TABLE_DATA_FILE_SUFFIX = ".txt";
    final static String TABLE_TEMP_FILE_PREFIX = "TB_TEMP_";

    final static String DATA_BASE_DIRECTORY = "storage/";
    final static String PATH_SEPARATOR = "/";

    private Metadata metadata;
    private List<Column> columns;

    //    Write given rows to the table file and returns the number of rows affected
    @Override
    public List<Row> insert(TableQuery query) throws Exception {
        Operation operation = query.getTableOperation();
        if (!Operation.INSERT.equals(operation)) {
            throw new InvalidOperation("Invalid operation");
        }
        if (!validateQuery(query)) {
            throw new Exception("Malformed query " + Operation.INSERT.name());
        }
        List<Row> rowsToInsert = query.getRows();
        Metadata metadataInsert = new MetadataServiceImpl().read(Entity.TABLE, query.getSchemaName());
        Table table =  metadataInsert.getTableByName(query.getTableName());
        Column primaryKey = table.getPrimaryKey();

        for (Row row : rowsToInsert) {
            Field primaryKeyField = row.getFieldByColumnName(primaryKey.getName());
            Condition primaryKeyCondition = Condition.builder().operand1(primaryKey.getName())
                    .operator(Operator.EQUALS).operand2(primaryKeyField.getValue().toString()).build();
            TableQuery selectQuery = TableQuery.builder()
                    .schemaName(query.getSchemaName())
                    .tableName(query.getTableName())
                    .columns(query.getColumns())
                    .tableOperation(Operation.SELECT)
                    .conditions(List.of(primaryKeyCondition))
                    .build();
            List<Row> rowsSelected = read(selectQuery);
            if (rowsSelected.size() > 0) {
                throw new Exception("Primary Key constraint violated");
            }
        }

        List<String> rowStrings = generateRowString(rowsToInsert, query.getSchemaName(), query.getTableName());
//        rowStrings.forEach(System.out::println);

        if (Objects.nonNull(rowStrings) && (rowStrings.size() > 0)) {
            String dataFilePath = getDataFilePath(query.getSchemaName(), query.getTableName());
            Path tableFilePath = Path.of(dataFilePath);
            appendToFile(tableFilePath, rowStrings);
        }
        return rowsToInsert;
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

    private List<String> generateRowString(List<Row> rows, String schemaName, String tableName) {
        List<String> rowStrings = new ArrayList<>();
        metadata = new MetadataServiceImpl().read(Entity.DATABASE, schemaName);
        Table table = metadata.getTableByName(tableName);
        StringBuilder rowString = new StringBuilder();
        List<Column> columns = table.getColumns();
        for (Row row : rows) {
            for (Column column : columns) {
                Optional<Field> optionalField = row.getAllFieldsOfRow().stream()
                        .filter(field -> column.getName().equalsIgnoreCase(field.getColumn().getName())).findFirst();
                if (optionalField.isPresent()) {
                    String columnValue = (String) optionalField.get().getValue();
                    if (Objects.nonNull(columnValue) && !columnValue.isBlank()) {
                        columnValue = columnValue.replace("|", "\\|");
                    }
//                    need to make changes here
                    rowString.append(columnValue).append(PIPE_DELIMITER);
                }
                else {
                    rowString.append(PIPE_DELIMITER);
                }
            }
            rowStrings.add(rowString.toString());
            rowString = new StringBuilder();
        }
        return rowStrings;
    }

    private boolean validateQuery(TableQuery query) throws Exception {
        boolean isValid = true;
        String tableName = query.getTableName();
        String schemaName = query.getSchemaName();
        if (Objects.isNull(tableName) || Objects.isNull(schemaName)) {
            return false;
        }
        switch (query.getTableOperation()) {
            case INSERT:
                List<Row> rows = query.getRows();
                if (Objects.isNull(rows) || (rows.size() <= 0)) {
                    return false;
                }
                for (Row row : rows) {
                    for (Field field : row.getAllFieldsOfRow()) {
                        if (field.getColumn() == null || field.getValue() == null || field.getColumn().getName() == null) {
                            return false;
                        }
                    }
                }
                break;

            case UPDATE:
                if (Objects.isNull(query.getFields()) || query.getFields().isEmpty()) {
                    return false;
                }
                for (Field field : query.getFields()) {
                    if (Objects.isNull(field.getColumn()) || Objects.isNull(field.getColumn().getName())) {
                        return false;
                    }
                }
                break;

            case DELETE:
//              DELETE query validation
                break;
        }
        return isValid;
    }

    @Override
    public List<Row> update(TableQuery query) throws Exception {
        Operation operation = query.getTableOperation();
        if (!Operation.UPDATE.equals(operation)) {
            throw new InvalidOperation("Invalid operation");
        }
        validateQuery(query);
        return processUpdate(query);
    }

    private synchronized List<Row> processUpdate(TableQuery query) throws Exception {
        List<Row> updatedRows = new ArrayList<>();
        TableQuery selectQuery = query;
        selectQuery.setTableOperation(Operation.SELECT);
        List<Row> rows = read(selectQuery);
        if (rows.isEmpty()) {
            return updatedRows;
        }
        List<String> stringRows = generateRowString(rows, query.getSchemaName(), query.getTableName());

        String tableFileName = getDataFilePath(query.getSchemaName(), query.getTableName());
        String tempFileName = getTempFilePath(query.getSchemaName(), query.getTableName());
        Path tableFilePath = Paths.get(tableFileName);
        Path tempFilePath = Path.of(tempFileName);
        Files.createFile(tempFilePath);
//        if set to true need to rollback the entire operation
        AtomicBoolean isException = new AtomicBoolean(false);

        try {
            Files.lines(tableFilePath).map(line -> {
                if (stringRows.contains(line)) {
//                    line to row
                    Map<Integer, String> map = getColumnValuesFromRowLines(line);
                    Row oldEntry = generateRow(map, columns);
//                    update the value here
                    Row newEntry = updateRows(oldEntry, query.getFields());
                    updatedRows.add(newEntry);

//                    line to line
                    return generateRowString(Arrays.asList(newEntry), query.getSchemaName(), query.getTableName()).stream().findFirst().get();
                } else
                {
                    return line;
                }
            }).forEach(line -> {
                try {
                    appendToFile(tempFilePath, List.of(line));
                } catch (Exception exception) {
                    System.out.println("Exception while writing to new data file");
                    isException.set(true);
                    exception.printStackTrace();
                }
            });
        }
        finally {
            if(Files.exists(tableFilePath) && Files.exists(tempFilePath) && !isException.get()) {
                Files.move(tempFilePath, tableFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            else {
//                Perform necessary cleanup on exception
            }
        }
        return updatedRows;
    }

    private Row updateRows(Row dbRow, List<Field> input) {
        Row updatedRow = new Row(dbRow);
        for (Field field : input) {
            Field dbField = updatedRow.getFieldByColumnName(field.getColumn().getName());
            if (Objects.isNull(field.getValue()) || field.getValue().toString().isBlank()) {
                dbField.setValue(null);
            }
            else {
                dbField.setValue(field.getValue());
            }
        }
        return updatedRow;
    }
    @Override
    public List<Row> delete(TableQuery query) throws Exception {
        Operation operation = query.getTableOperation();
        if (!Operation.DELETE.equals(operation)) {
            throw new InvalidOperation("Invalid operation");
        }
        validateQuery(query);
        return processDelete(query);
    }

    @Override
    public Boolean drop(TableQuery query) throws Exception {
        Operation operation = query.getTableOperation();
        if (!Operation.DROP.equals(operation)) {
            throw new InvalidOperation("Invalid operation " + operation.name());
        }
        MetadataService metadataService = new MetadataServiceImpl();
        Metadata metadataDB = metadataService.read(Entity.DATABASE, query.getSchemaName());

        Metadata metadataTable = new Metadata();
        metadataTable.setDatabase(metadataDB.getDatabase());
        metadataTable.getDatabase().setTables(List.of(metadataDB.getTableByName(query.getTableName())));

        metadataService.delete(Entity.TABLE, metadataTable);
        return Boolean.TRUE;
    }

    @Override
    public Boolean create(TableQuery query) throws Exception {
        Operation operation = query.getTableOperation();
        if (!Operation.CREATE.equals(operation)) {
            throw new InvalidOperation("Invalid operation " + operation.name());
        }
        Metadata metadataTable = new Metadata();
        List<Table> tablesToCreate = new ArrayList<>();
        tablesToCreate.add(query.getTable());

        Database database = Database.builder().name(query.getSchemaName()).tables(tablesToCreate).build();

        metadataTable.setDatabase(database);
        MetadataService metadataService = new MetadataServiceImpl();
        metadataService.write(Entity.TABLE, metadataTable);
        return Boolean.TRUE;
    }

    private synchronized List<Row> processDelete(TableQuery query) throws Exception {
        TableQuery selectQuery = query;
        selectQuery.setTableOperation(Operation.SELECT);
        List<Row> rows = read(selectQuery);
        if (rows.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> stringRows = generateRowString(rows, query.getSchemaName(), query.getTableName());
        String tableFileName = getDataFilePath(query.getSchemaName(), query.getTableName());
        String tempFileName = getTempFilePath(query.getSchemaName(), query.getTableName());
        Path tableFilePath = Paths.get(tableFileName);
        Path tempFilePath = Path.of(tempFileName);
        Files.createFile(tempFilePath);
//        if set to true need to rollback the entire operation
        AtomicBoolean isException = new AtomicBoolean(false);
        try {
            Files.lines(tableFilePath).filter(row -> {
                if (stringRows.contains(row)) {
                    return false;
                } else {
                    return true;
                }
            }).forEach(line -> {
                try {
                    appendToFile(tempFilePath, List.of(line));
                } catch (Exception exception) {
                    System.out.println("Exception while writing to new data file");
                    isException.set(true);
                    exception.printStackTrace();
                }
            });
        }
        finally {
            if(Files.exists(tableFilePath) && Files.exists(tempFilePath) && !isException.get()) {
                Files.move(tempFilePath, tableFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            else {
//                Perform necessary cleanup on exception
            }
        }
        return rows;
    }

    /*
     *  This method will read data from data file
     *  Returns the rows as per the column list and operands provided
     *  This method is to be used by query processor
     */
    @Override
    public List<Row> read(TableQuery query) throws Exception {
        Operation operation = query.getTableOperation();
        if (!Operation.SELECT.equals(operation)) {
            throw new InvalidOperation("Invalid operation");
        }
        String schemaName = query.getSchemaName();
        metadata = new MetadataServiceImpl().read(Entity.DATABASE, schemaName);
        columns = metadata.getAllColumnsForTable(query.getTableName());
        List<Row> rows = new ArrayList<>();

        String tableName = query.getTableName();
        String dataFilePath = getDataFilePath(schemaName, tableName);
        Path filePath = Paths.get(dataFilePath);
        try {
            rows = Files.lines(filePath)
                    .map(line -> getColumnValuesFromRowLines(line))
                    .map(map -> generateRow(map, columns))
                    .filter(Objects::nonNull)
                    .filter(row -> filterRow(row, query.getConditions()))
                    .map(row -> getRequiredColumns(row, query.getColumns()))
                    .collect(Collectors.toList());
        } catch (Exception ioException) {
            ioException.printStackTrace();
            System.out.println("Exception while reading file " + dataFilePath);
        }
        return rows;
    }

    private String getDataFilePath(String schemaName, String tableName) {
        String dataFilePath = DATA_BASE_DIRECTORY +
                schemaName.toUpperCase(Locale.ROOT) + PATH_SEPARATOR +
                TABLE_DATA_FILE_PREFIX + tableName.toUpperCase(Locale.ROOT) +
                TABLE_DATA_FILE_SUFFIX;
        return dataFilePath;
    }

    private String getTempFilePath(String schemaName, String tableName) {
        String dataFilePath = DATA_BASE_DIRECTORY +
                schemaName.toUpperCase(Locale.ROOT) + PATH_SEPARATOR +
                TABLE_TEMP_FILE_PREFIX + tableName.toUpperCase(Locale.ROOT) +
                TABLE_DATA_FILE_SUFFIX;
        return dataFilePath;
    }

    private Row generateRow(Map<Integer, String> rowValue, List<Column> columns) {
//        Generate rows and filter what is required
        Row row = new Row();
        if (rowValue.size() != columns.size()) {
            System.out.println("Something is wrong here");
        }
        for (int i = 1, j = 0; i <= rowValue.size(); i++, j++) {
            String value = rowValue.get(i);
            Column column = columns.get(j);
            Field field = new Field(column, value);
            row.addField(field);
        }
//        row.getAllFieldsOfRow().stream().forEach(field -> System.out.println("Column:" + field.getColumn().getName() +
//                " Value :" + field.getValue()));
        return row;
    }

    private boolean filterRow(Row row, List<Condition> conditions) {
        if (Objects.isNull(conditions) || conditions.isEmpty()) {
            return true;
        }
        boolean matchesCondition = false;
        OperandProcessor processor = new OperandProcessorImpl();
        for (Field field : row.getAllFieldsOfRow()) {
            for (Condition condition : conditions) {
                if (condition.getOperand1().equalsIgnoreCase(field.getColumn().getName())) {
                    matchesCondition = processor.process(field, condition);
                }
            }
        }
        return matchesCondition;
    }

    private Row getRequiredColumns(Row row, List<Column> requiredColumns) {
        if (requiredColumns == null ||  requiredColumns.isEmpty()) {
            return new Row(row);
        }
        Row requiredRow = new Row();
        List<String> columnNames = requiredColumns.stream()
                .map(c -> c.getName()).distinct().collect(Collectors.toList());
        for (Field field : row.getAllFieldsOfRow()) {
            if (columnNames.contains(field.getColumn().getName())) {
                requiredRow.addField(field);
            }
        }
        return requiredRow;
    }

    private Map<Integer, String> getColumnValuesFromRowLines(String line) {
        Map<Integer, String> columnValues = new HashMap<>();
        if (Objects.isNull(line) || line.isEmpty()) {
            return columnValues;
        }
        int length = line.length();

        Integer columnCount = 0;
        StringBuilder val = new StringBuilder();
        for (int pos = 0; pos < length; pos++) {
            char c = line.charAt(pos);
            if (PIPE_DELIMITER == c || ESCAPE_CHAR == c || pos == length - 1) {
                if (ESCAPE_CHAR == c && line.charAt(pos + 1) == PIPE_DELIMITER) {
                    val.append(PIPE_DELIMITER);
                    pos = pos + 1;
                    continue;
                }
//                Adding a column value
                if (pos == length - 1 && PIPE_DELIMITER != c) {
                    val.append(c);
                }
                final Integer countNo = ++columnCount;
                columnValues.put(countNo, val.toString());
                val = new StringBuilder();
            } else {
                val.append(c);
            }
        }
//        columnValues.forEach((k, v) -> System.out.println("{k:" + k + "} {v:" + v + "}"));
        return columnValues;
    }

}
