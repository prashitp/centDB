package com.example.services.metadata;

import com.example.models.Metadata;

public interface MetadataService {

    Metadata readMetadataForDatabase(String databaseName);

    boolean writeMetadataToFile(Metadata metadata);

}
