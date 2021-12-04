package com.example.services.accessor;

import com.example.models.Row;
import com.example.models.TableQuery;

import java.util.List;

public interface FileDataAccessor {

    List<Row> readDataFromTable(TableQuery query) throws Exception;

    int insertRowsToTable(TableQuery table) throws Exception;

    int updateRowsOfTable(TableQuery table) throws Exception;

    int deleteRowsOfTable(TableQuery query) throws Exception;

}
