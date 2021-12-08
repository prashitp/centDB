package com.example.services.logs;

import com.example.models.Metadata;
import com.example.models.Row;
import com.example.models.Table;
import com.example.models.context.LogContext;
import lombok.extern.java.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EventLogService extends LogService {
	private EventLogService() {
		super("storage/logs/event_log.txt");
	}

	static LogService eventLogService;

	public static LogService getInstance() {
		if(eventLogService != null) {
			return eventLogService;
		}
		return new EventLogService();
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

		if(!LogContext.getDatabaseState().isEmpty()) {
			String databaseState = LogContext.getDatabaseState();
			prefix = prefix.concat(String.format("Database State: %s; ", databaseState));
		}

		if (Objects.nonNull(LogContext.getMetadata())) {
			Metadata metadata = LogContext.getMetadata();
			if (Objects.nonNull(metadata.getDatabaseName())) {
				List<Table> tables = metadata.getAllTablesFromDatabase();
				String tableInfo = String.format("Tables: ");
				for (Table table : tables) {
					List<Row> rows = table.getRows();
					tableInfo = tableInfo.concat(String.format("%s (%d); ",table.getName(),table.getRows().size()));
				}
				prefix = prefix.concat(tableInfo);
			}
		}

		if(!LogContext.getQuery().isEmpty()) {
			String query = LogContext.getQuery();
			prefix = prefix.concat(String.format("Query: %s; ", query));
		}

		if(LogContext.getExecutionTime() != 0) {
			long executionTime = LogContext.getExecutionTime();
			prefix = prefix.concat(String.format("Execution Time: %s ms; ", executionTime));
		}

		return prefix;
	}
}
