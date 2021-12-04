package com.example.services.metadata;

import com.example.models.Metadata;
import com.example.models.enums.Entity;

public interface MetadataService {

    Metadata read(Entity entity, String databaseName);

    boolean write(Metadata metadata);

}
