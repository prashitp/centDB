package com.example.services.accessor;

import com.example.exceptions.InvalidOperation;
import com.example.models.*;
import com.example.models.enums.Entity;

import com.example.services.metadata.MetadataServiceImpl;
import com.example.models.enums.Operation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class FileAccessorImpl implements TableAccessor {

    final static char PIPE_DELIMITER = '|';
    final static char ESCAPE_CHAR = '\\';

    final static String TABLE_DATA_FILE_PREFIX = "TB_";
    final static String TABLE_DATA_FILE_SUFFIX = ".txt";

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
        String schemaName = query.getSchemaName();;
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
                break;

            case DELETE:
                break;
        }
        return isValid;

    }

    @Override
    public int update(TableQuery query) throws Exception {
        Operation operation = query.getTableOperation();
        if (!Operation.UPDATE.equals(operation)) {
            throw new InvalidOperation("Invalid operation");
        }
        return 0;
    }

    @Override
    public int delete(TableQuery query) throws Exception {
        Operation operation = query.getTableOperation();
        if (!Operation.DELETE.equals(operation)) {
            throw new InvalidOperation("Invalid operation");
        }
        return 0;
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
                    .map(map -> generateRow(map))
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

    private Row generateRow(Map<Integer, String> rowValue) {
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
        if (requiredColumns.isEmpty()) {
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
        if (Objects.nonNull(line) && line.isEmpty()) {
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
