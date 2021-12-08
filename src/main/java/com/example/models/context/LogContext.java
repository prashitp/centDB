package com.example.models.context;


import com.example.models.Metadata;
import com.example.models.Row;
import com.example.models.Table;
import com.example.models.User;
import com.example.models.enums.Operation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class LogContext {

    private static Metadata metadata;

    private static Table table;

    private static User user;

    private static String query;

    private static long executionTime;

    private static String databaseState;

    private static List<Table> databaseTables;

    public static Metadata getMetadata() {
        return metadata;
    }

    public static Table getTable() {
        return table;
    }

    public static User getUser() {
        return user;
    }

    public static String getQuery() {
        return query;
    }

    public static long getExecutionTime() {
        return executionTime;
    }

    public static String getDatabaseState() {
        return databaseState;
    }

    public static List<Table> getDatabaseTables() {
        return databaseTables;
    }

    public static void setMetadata(Metadata metadata) {
        LogContext.metadata = metadata;
    }

    public static void setTable(Table table) {
        LogContext.table = table;
    }

    public static void setUser(User user) {
        LogContext.user = user;
    }

    public static void setQuery(String query) {
        LogContext.query = query;
    }

    public static void setExecutionTime(long milliSeconds) {
        executionTime = milliSeconds;
    }

    public static void setDatabaseState(String databaseState) {
        LogContext.databaseState = databaseState;
    }

    public static void setDatabaseTables(List<Table> tables) {
        LogContext.databaseTables = tables;
    }
}
