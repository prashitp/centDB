package com.example.services.logs;

import com.example.models.Metadata;
import com.example.models.context.LogContext;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class QueryLogService extends LogService {

	private QueryLogService() {
		super("storage/logs/query_log.txt");
	}

	static LogService queryLogService;

	public static LogService getInstance() {
		if(queryLogService != null) {
			return queryLogService;
		}
		return new QueryLogService();
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

	private String prefix() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		String date = format.format(new Date(System.currentTimeMillis()));
		String prefix = String.format("User: %s, Timestamp: %s, ", LogContext.getUser().getUsername(), date);

		if (Objects.nonNull(LogContext.getMetadata())) {
			Metadata metadata = LogContext.getMetadata();
			if (Objects.nonNull(metadata.getDatabaseName())) {
				String databaseName = metadata.getDatabaseName();
				prefix = prefix.concat(String.format("Database: %s, ", databaseName));
			}
		}

		if (Objects.nonNull(LogContext.getTable())) {
			String tableName = LogContext.getTable().getName();
			prefix = prefix.concat(String.format("Table: %s, ", tableName));
		}
		return prefix;
	}
}
