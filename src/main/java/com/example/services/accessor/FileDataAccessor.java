package com.example.services.accessor;

import com.example.models.Row;
import com.example.models.Table;
import com.example.models.TableQuery;

import java.util.List;

public interface FileDataAccessor {

    List<Row> readDataFromTable(TableQuery query);

    int writeRowsToTheTable(Table table);

}