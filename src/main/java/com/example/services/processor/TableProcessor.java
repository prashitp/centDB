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

    @SneakyThrows
    public List<Row> delete(TableQuery tableQuery) {
       return tableAccessor.delete(tableQuery);
    }

    @SneakyThrows
    public List<Row> update(TableQuery tableQuery) {
        return tableAccessor.update(tableQuery);
    }

    @SneakyThrows
    public List<Row> insert(TableQuery tableQuery) {
        return tableAccessor.insert(tableQuery);
    }

    @SneakyThrows
    public Boolean drop(TableQuery tableQuery) {
        tableAccessor.drop(tableQuery);
        return true;
    }

    @SneakyThrows
    public Boolean create(TableQuery tableQuery) {
        tableAccessor.create(tableQuery);
        return true;
    }
}
