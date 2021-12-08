package com.example.services.logs;

import com.example.models.Row;
import com.example.models.Table;
import com.example.models.context.LogContext;
import com.example.models.Metadata;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class GeneralLogService extends LogService{

    private GeneralLogService() {
        super("storage/logs/general_log.txt");
    }

    static LogService generalLogService;

    public static LogService getInstance() {
        if(generalLogService != null) {
            return generalLogService;
        }
        return new GeneralLogService();
    }
    public void log(String string) {
        try {
            String log = prefix().concat("Message: ").concat(string).concat("\n");
            fileWriter.write(log);
            fileWriter.flush();
        } catch (IOException e) {
            System.out.println("Error logging");
        }
    }

    public String prefix() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        String date = format.format(new Date(System.currentTimeMillis()));
        String prefix = String.format("User: %s, Timestamp: %s; ", LogContext.getUser().getUsername(), date);

        if (Objects.nonNull(LogContext.getMetadata())) {
            Metadata metadata = LogContext.getMetadata();
            if (Objects.nonNull(metadata.getDatabaseName())) {
                String databaseName = metadata.getDatabaseName();
                Integer tableCount = metadata.getAllTablesFromDatabase().size();
                prefix = prefix.concat(String.format("Database: %s (%d); ", databaseName, tableCount));
            }
        }

        if (Objects.nonNull(LogContext.getMetadata())) {
            Metadata metadata = LogContext.getMetadata();
            if (Objects.nonNull(metadata.getDatabaseName())) {
                List<Table> tables = metadata.getAllTablesFromDatabase();
                String tableInfo = String.format("Tables:");
                for (Table table : tables) {
                    List<Row> rows = table.getRows();
                    tableInfo = tableInfo.concat(String.format(" %s (%d)",table.getName(),table.getRows().size()));
                }
                prefix = prefix.concat(tableInfo+"; ");
            }
        }

        if(LogContext.getExecutionTime() != 0) {
            long executionTime = LogContext.getExecutionTime();
            prefix = prefix.concat(String.format("Execution Time: %s ms; ", executionTime));
        }

        return prefix;
    }
}
