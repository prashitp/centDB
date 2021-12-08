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
    public Boolean delete(TableQuery tableQuery) {
        tableAccessor.delete(tableQuery);
        return true;
    }

    @SneakyThrows
    public Boolean update(TableQuery tableQuery) {
        tableAccessor.update(tableQuery);
        return true;
    }

    @SneakyThrows
    public Boolean insert(TableQuery tableQuery) {
        tableAccessor.insert(tableQuery);
        return true;
    }

    @SneakyThrows
    public Boolean drop(TableQuery tableQuery) {
        //tableAccessor.drop(tableQuery);
        return true;
    }

    @SneakyThrows
    public Boolean create(TableQuery tableQuery) {
        //tableAccessor.create(tableQuery);
        return true;
    }
}
