package com.example.services.parser;

import com.example.models.Metadata;
import com.example.models.enums.Entity;
import com.example.services.metadata.MetadataService;
import com.example.util.StringUtil;
import lombok.AllArgsConstructor;

import static com.example.util.Constants.USE;

@AllArgsConstructor
public class DatabaseParser {

    private MetadataService metadataService;

    public Metadata use(String query) {
        String database = StringUtil.matchFrom(query, USE);
        return metadataService.read(Entity.DATABASE, database);
    }
}
