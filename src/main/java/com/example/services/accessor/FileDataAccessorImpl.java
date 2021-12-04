package com.example.services.accessor;

import com.example.models.*;
import com.example.models.enums.Entity;
import com.example.services.metadata.DatabaseMetadataServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class FileDataAccessorImpl implements FileDataAccessor {

    final static char PIPE_DELIMITER = '|';
    final static char ESCAPE_CHAR = '\\';

    final static String TABLE_DATA_FILE_PREFIX = "TB_";
    final static String TABLE_DATA_FILE_SUFFIX = ".txt";

    final static String DATA_BASE_DIRECTORY = "userData";
    final static String PATH_SEPARATOR = "/";

    private Metadata metadata;

    private List<Column> columns;

    @Override
    public int writeRowsToTheTable(Table table) {
        return -1;
    }

    @Override
    public List<Row> readDataFromTable(TableQuery query) {
        String schemaName = query.getSchemaName();
        metadata = new DatabaseMetadataServiceImpl().read(Entity.DATABASE, schemaName);
        columns = metadata.getAllColumnsForTable(query.getTableName());
        List<Row> rows = new ArrayList<>();

        String tableName = query.getTableName();
        String dataFilePath = DATA_BASE_DIRECTORY + PATH_SEPARATOR +
                schemaName.toUpperCase(Locale.ROOT) + PATH_SEPARATOR +
                TABLE_DATA_FILE_PREFIX + tableName.toUpperCase(Locale.ROOT) +
                TABLE_DATA_FILE_SUFFIX;
        Path filePath = Paths.get(dataFilePath);
        try {
            rows = Files.lines(filePath)
                    .map(line -> getColumnValuesFromRowLines(line))
                    .map(map -> generateRow(map))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.out.println("Exception while reading file " + dataFilePath);
        }
        return rows;
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
        row.getAllFieldsOfTheRow().stream().forEach(field -> System.out.println("Column:" +field.getColumn().getName() +
                            " Value :" + field.getValue()));
        return row;
    }

//        Write logic here to filter the required columns from the Table query
//        And the Conditions of the query
    private Row getRequiredRows(Row row, TableQuery query) {
        if (query.getColumns().isEmpty()) {
            return new Row(row);
        }
        List<String> requiredColumns = query.getColumns().stream().map(c -> c.getName()).collect(Collectors.toList());
        List<Condition> conditions = query.getConditions();
        Row requiredRow = new Row();
        for (Field field : row.getAllFieldsOfTheRow()) {
            for (Condition condition : conditions) {
//                Process operand
//                User interface IOperandProcessor (its under implementation)
            }
            if (requiredColumns.contains(field.getColumn().getName())) {
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
                if (ESCAPE_CHAR == c && line.charAt(pos+1) == PIPE_DELIMITER) {
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

        columnValues.forEach((k,v) -> System.out.println("{k:" + k + "} {v:" +v + "}"));
        return columnValues;
    }

}
