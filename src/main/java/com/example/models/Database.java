package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Database {

    private String name;

    private List<Table> tables;

    public void addTable(Table table) {
        if (Objects.isNull(tables)) {
            tables = new ArrayList<>();
        }
        tables.add(table);
    }

    public void updateTables(List<Table> tables) {
        this.tables = tables;
    }

}
