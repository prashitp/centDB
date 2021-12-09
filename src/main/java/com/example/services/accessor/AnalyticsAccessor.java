package com.example.services.accessor;

import java.util.HashMap;

public interface AnalyticsAccessor {
	HashMap<String, Integer> getTotalQueries(String filePath, String databaseName);

	HashMap<String, Integer> getQueriesByType(String filePath, String databaseName, String type);
}
