package com.example.services.processor;

import com.example.models.Condition;
import com.example.models.Row;
import com.example.models.TableQuery;
import com.example.models.enums.Operation;
import com.example.models.enums.Operator;
import com.example.services.accessor.FileAccessorImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class TableProcessorTest {

    private TableProcessor tableProcessor;

    @Before
    public void beforeEach() {
        tableProcessor = new TableProcessor(new FileAccessorImpl());
    }


    @Test
    public void selectQueryTest() {
        TableQuery tableQuery = TableQuery.builder()
                .tableOperation(Operation.SELECT)
                .tableName("BIRDS")
                .schemaName("CENT_DB1")
                .conditions(Collections.singletonList(Condition.builder()
                        .operand1("BIRD_ID")
                        .operator(Operator.EQUALS)
                        .operand2("1")
                        .build()))
                .build();

        List<Row> rows = tableProcessor.select(tableQuery);
    }
}
