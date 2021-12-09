package com.example.services.accessor;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class AnalyticsAccessorImpl implements AnalyticsAccessor {
	@SneakyThrows
	public HashMap<String, Integer> getTotalQueries(String filePath, String databaseName) {
		HashMap<String, Integer> userFrequency = new HashMap<>();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] strings = line.split(";");

			String databaseField = strings[2];
			String messageField = strings[4];

			String userField = strings[0];
			String user = userField.split(":")[1].trim();

			if (validate(databaseField, databaseName)) {
				if(messageField.contains("started")) {
					if(userFrequency.containsKey(user)) {
						userFrequency.put(user, userFrequency.get(user) + 1);
					} else {
						userFrequency.put(user, 1);
					}

				}
			}
		}
		return userFrequency;
	}

	private boolean validate(String field, String input) {
		String value = field.split(":")[1].trim();
		if(input.equals(value)) {
			return true;
		}
		return false;
	}


	@SneakyThrows
	public HashMap<String, Integer> getQueriesByType(String filePath, String databaseName, String type) {
		HashMap<String, Integer> tableFrequency = new HashMap<>();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] strings = line.split(";");

			String databaseField = strings[2];
			String dbNameLog = databaseField.split(":")[1].trim();

			String tableField = strings[3];
			String tableName = tableField.split(":")[1].trim();

			String messageField = strings[4];
			if(messageField.split("-").length > 1) {
				String query = messageField.split("-")[1].trim();
				String queryType = query.split("\\s")[0].trim();
				if (databaseName.equals(dbNameLog)) {
					if(queryType.equals(type)) {
						if(tableFrequency.containsKey(tableName)) {
							tableFrequency.put(tableName, tableFrequency.get(tableName) + 1);
						} else {
							tableFrequency.put(tableName, 1);
						}
					}
				}
			}

		}
		return tableFrequency;
	}
}
