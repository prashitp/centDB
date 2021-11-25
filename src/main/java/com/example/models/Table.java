package com.example.models;

import com.sun.istack.internal.NotNull;
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

    @NotNull
    private String name;

    private List<Column> columns;

    private Column primaryKey;

    private List<Field> rows;
}
