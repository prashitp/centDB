package com.example.services.parser;

import com.example.models.Database;
import com.example.models.Metadata;
import com.example.models.enums.Entity;
import com.example.services.metadata.MetadataService;
import com.example.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import static com.example.util.Constants.CREATE_DATABASE;
import static com.example.util.Constants.USE;

@AllArgsConstructor
public class DatabaseParser {

    private MetadataService metadataService;

    public Metadata use(String query) {
        String database = StringUtil.matchFrom(query, USE);
        return metadataService.read(Entity.DATABASE, database);
    }

    @SneakyThrows
    public Boolean create(String query) {
        Boolean isCreated = true;
        String database = StringUtil.matchFrom(query, CREATE_DATABASE);
        Metadata metadata = new Metadata();
        metadata.setDatabase(Database.builder()
                .name(database)
                .build());
        metadataService.write(Entity.DATABASE, metadata);
        return isCreated;
    }
}
