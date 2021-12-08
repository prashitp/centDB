package com.example.models.context;


import com.example.models.Metadata;
import com.example.models.Table;
import com.example.models.User;
import java.util.List;

public final class LogContext {

    private static Metadata metadata;

    private static Table table;

    private static User user;

    private static long executionTime;

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

    public static long getExecutionTime() {
        return executionTime;
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

    public static void setExecutionTime(long milliSeconds) {
        executionTime = milliSeconds;
    }

    public static void setDatabaseTables(List<Table> tables) {
        LogContext.databaseTables = tables;
    }
}
