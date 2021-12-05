package com.example.services.metadata;

import com.example.models.Metadata;
import com.example.models.enums.Entity;

public interface MetadataService {

    //    This method will read the metadata from the entire database
//    Entity supported here is only DATABASE
    Metadata read(Entity entity, String databaseName);

//    This method will write metadata to the metadata file
//    Metadata object needs to be populated with the appropriate values
//    that needs to written to the metadata file
//    Entity will specify what metadata needs to be written
//    Used for create new DB or tables
    void write(Entity entity, Metadata metadata) throws Exception;

    //    This method will remove metadata for the entity passed - that is Table or Database
//    To be used for dropping a table or database permanently
    void delete(Entity entity, Metadata metadata) throws Exception;

}
