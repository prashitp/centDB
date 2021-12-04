package com.example.services.accessor;

import com.example.models.Row;
import com.example.models.TableQuery;

import java.util.List;

public interface TableDataAccessor {

    List<Row> read(TableQuery query) throws Exception;

    int insert(TableQuery table) throws Exception;

    int update(TableQuery table) throws Exception;

    int delete(TableQuery query) throws Exception;

}