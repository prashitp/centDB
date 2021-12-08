package com.example.services.logs;

import com.example.models.Metadata;
import com.example.models.Row;
import com.example.models.Table;
import com.example.models.context.LogContext;

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
				prefix = prefix.concat(String.format("Database: %s; ", databaseName));
				prefix = prefix.concat(String.format("Tables: %d; ", tableCount));
			}
		}

		return prefix;
	}
}
