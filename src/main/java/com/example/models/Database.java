package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Database {

//    @NotNull
    private String name;

    private List<Table> tables;

    public void addTable(Table table) {
        if (Objects.isNull(tables)) {
            tables = new ArrayList<>();
        }
        tables.add(table);
    }

}
