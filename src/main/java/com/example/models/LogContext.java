package com.example.models;


public final class LogContext {

    private static Metadata metadata;

    private static Table table;

    private static User user;

    public static Metadata getMetadata() {
        return metadata;
    }

    public static Table getTable() {
        return table;
    }

    public static User getUser() {
        return user;
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
}
