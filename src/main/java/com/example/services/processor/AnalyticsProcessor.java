package com.example.services.processor;

import com.example.services.accessor.AnalyticsAccessor;
import com.example.services.accessor.AnalyticsAccessorImpl;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
public class AnalyticsProcessor {

	public HashMap<String, Integer> countAll(String query) {
		AnalyticsAccessor analyticsAccessor = new AnalyticsAccessorImpl();
		List<String> strings = Arrays.asList(query.split("\\s"));
		return analyticsAccessor.getTotalQueries("storage/logs/query_log.txt", strings.get(2));
	}

	public HashMap<String, Integer> countType(String query, String type) {
		AnalyticsAccessor analyticsAccessor = new AnalyticsAccessorImpl();
		List<String> strings = Arrays.asList(query.split("\\s"));
		return analyticsAccessor.getQueriesByType("storage/logs/query_log.txt", strings.get(2), type);
	}

}
