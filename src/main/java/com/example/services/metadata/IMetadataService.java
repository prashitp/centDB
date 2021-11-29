package com.example.services.metadata;

import com.example.models.Metadata;

public interface IMetadataService {

    Metadata readMetadataForDatabase(String databaseName);

}
