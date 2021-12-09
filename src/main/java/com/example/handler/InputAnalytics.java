package com.example.handler;

import com.example.models.enums.Analytics;
import com.example.services.processor.AnalyticsProcessor;
import com.example.util.Constants;

import java.util.*;

public class InputAnalytics {
	public static void query(Scanner scanner) {
		QUERY: do {
			try {
				System.out.print(">> ");
				final String query = scanner.nextLine();

				operate(query.toUpperCase(Locale.ROOT));

			} catch (Exception e) {
				continue QUERY;
			}
		} while (true);
	}
	public static void operate(String query) {
		List<String> strings = Arrays.asList(query.split("\\s"));
		Analytics analytics = Analytics.valueOf(strings.get(1).trim().toUpperCase(Locale.ROOT));

		if(strings.get(0).toUpperCase().equals(Constants.ANALYTICS_QUERY)) {
			analytics.accept(new Analytics.AnalyticsVisitor<Void>() {
				final AnalyticsProcessor analyticsProcessor = new AnalyticsProcessor();

				@Override
				public Void visitAllQueries() {
					String database = strings.get(2).toUpperCase();
					HashMap<String, Integer> count = analyticsProcessor.countAll(query);
					printAllQueriesMap(count, database);
					return null;
				}

				@Override
				public Void visitByType() {
					String type = strings.get(1).toUpperCase();
					HashMap<String, Integer> count = analyticsProcessor.countType(query, type);
					printTypeMap(count, type);
					return null;
				}

			});
		} else {
			System.out.println("Query not supported");
		}

	}

	private static void printTypeMap(HashMap<String,Integer> map, String type) {
		for (String table: map.keySet()) {
			System.out.printf("Total %d %s operations are performed on %s\n",map.get(table),type,table);
		}
	}

	private static void printAllQueriesMap(HashMap<String,Integer> map, String database) {
		for (String user: map.keySet()) {
			System.out.printf("User %s submitted %d queries %s\n",user,map.get(user),database);
		}
	}
}
