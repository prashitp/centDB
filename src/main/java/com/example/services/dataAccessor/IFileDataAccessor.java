package com.example.services.dataAccessor;

import com.example.models.Row;
import com.example.models.TableQuery;

import java.util.List;

public interface IFileDataAccessor {

    List<Row> readDataFromTable(TableQuery query);
}
