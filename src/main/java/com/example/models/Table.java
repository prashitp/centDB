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
public class Table {

    private String name;

    private List<Column> columns;

    private Column primaryKey;

    private List<Row> rows;

    private List<ForeignKey> foreignKeys;

    public void addForeignKey(ForeignKey foreignKey) {
        if (Objects.isNull(foreignKeys)) {
            foreignKeys = new ArrayList<>();
        }
        foreignKeys.add(foreignKey);
    }

    public boolean hasForeignKey() {
        if (Objects.isNull(foreignKeys) || foreignKeys.isEmpty()) {
            return false;
        }
        else if (foreignKeys.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
