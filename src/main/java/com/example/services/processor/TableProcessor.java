package com.example.services.processor;

import com.example.models.*;
import com.example.services.accessor.TableAccessor;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;

@AllArgsConstructor
public class TableProcessor {

    private TableAccessor tableAccessor;

    @SneakyThrows
    public List<Row> select(TableQuery tableQuery) {
        return tableAccessor.read(tableQuery);
    }




}
