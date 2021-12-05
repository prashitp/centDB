package com.example.services.metadata;

import com.example.models.Column;
import com.example.models.Database;
import com.example.models.Metadata;
import com.example.models.Table;
import com.example.models.enums.Entity;

import java.util.Locale;

public abstract class AbstractMetadataService implements MetadataService {

    protected enum MetadataToken {
        DB, TB, CL, PK
    }

    protected final static char PIPE_DELIMITER = '|';
    protected final static String DATA_BASE_DIRECTORY = "userData/";
    protected final static String METADATA_BASE_DIRECTORY = "metadata/";
    protected final static String METADATA_FILE_PREFIX = "MD_";
    protected final static String METADATA_FILE_EXTENSION = ".txt";
    protected final static String PATH_SEPARATOR = "/";

    protected static String TABLE_DATA_FILE_PREFIX = "TB_";
    protected static String TABLE_DATA_FILE_SUFFIX = ".txt";

    @Override
    public abstract Metadata read(Entity entity, String databaseName);

    @Override
    public abstract void write(Entity entity, Metadata metadata) throws Exception ;

    @Override
    public abstract void delete(Entity entity, Metadata metadata) throws Exception;

    protected String entryBuilder(Database database) {
        return new StringBuilder()
                .append(MetadataServiceImpl.MetadataToken.DB.name())
                .append(PIPE_DELIMITER)
                .append(database.getName().toUpperCase(Locale.ROOT)).toString();
    }

    protected String entryBuilder(Table table) {
        return new StringBuilder()
                .append(MetadataServiceImpl.MetadataToken.TB.name())
                .append(PIPE_DELIMITER)
                .append(table.getName().toUpperCase(Locale.ROOT)).toString();
    }

    protected String entryBuilder(Column column) {
        return new StringBuilder()
                .append(MetadataServiceImpl.MetadataToken.CL)
                .append(PIPE_DELIMITER)
                .append(column.getName().toUpperCase(Locale.ROOT))
                .append(PIPE_DELIMITER)
                .append(column.getDataType().toUpperCase(Locale.ROOT)).toString();
    }

    protected String entryBuilderPrimaryKey(Column column) {
        return new StringBuilder()
                .append(MetadataToken.PK)
                .append(PIPE_DELIMITER)
                .append(column.getName().toUpperCase(Locale.ROOT)).toString();
    }

    protected String getTableDataFilePath(String databaseName, String tableName) {
        return DATA_BASE_DIRECTORY +
                databaseName.toUpperCase(Locale.ROOT) +
                PATH_SEPARATOR +
                TABLE_DATA_FILE_PREFIX +
                tableName.toUpperCase(Locale.ROOT) +
                TABLE_DATA_FILE_SUFFIX;
    }

    protected String getMetadataFilePath(String databaseName) {
        return METADATA_BASE_DIRECTORY +
                METADATA_FILE_PREFIX +
                databaseName.toUpperCase(Locale.ROOT) +
                METADATA_FILE_EXTENSION;
    }

    protected String getDirectoryPath(Database database) {
        return DATA_BASE_DIRECTORY +
                database.getName().toUpperCase(Locale.ROOT);
    }

}
