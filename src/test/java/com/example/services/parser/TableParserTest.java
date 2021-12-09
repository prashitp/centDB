package com.example.services.parser;

import com.example.models.Database;
import com.example.models.Metadata;
import org.junit.Test;

public class TableParserTest {

    @Test
    public void tableCreationTest() {
        TableParser tableParser = new TableParser();
        String query = "CREATE TABLE PERSONS (PersonID int,LastName VARCHAR,FirstName VARCHAR,Address VARCHAR,City VARCHAR, PRIMARY KEY PersonID, FOREIGN KEY PersonID REFERENCES BIRDS(BIRD_ID))";
        Metadata metadata = new Metadata();
        metadata.setDatabase(Database.builder()
                .name("CENT_DB1")
                .build());
        tableParser.create(query, metadata);
    }
}
