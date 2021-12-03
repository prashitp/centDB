package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Table {

    private String name;

    private List<Column> columns;

    private Column primaryKey;

    private List<Row> rows;
}
