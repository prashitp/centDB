package com.example.services.processor;

import com.example.services.accessor.FileAccessorImpl;
import com.example.services.metadata.DatabaseMetadataServiceImpl;
import org.junit.Before;
import org.junit.Test;

public class TableProcessorTest {

    private TableProcessor tableProcessor;

    @Before
    public void beforeEach() {
        tableProcessor = new TableProcessor(new DatabaseMetadataServiceImpl(), new FileAccessorImpl());
    }


    @Test
    public void selectQueryTest() {
        tableProcessor.select("SELECT * FROM users WHERE username = 'test_user'");
    }
}
