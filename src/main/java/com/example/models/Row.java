package com.example.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Row {

    private List<Field> rowData;

    public Row() {
        rowData = new ArrayList<Field>();
    }

    public Row(Row row) {
        this.rowData = new ArrayList<>(row.getAllFieldsOfRow());
    }

    public Row(List<Field> rowData) {
        super();
        this.rowData = rowData;
    }

    public void addField(Field field) {
        this.rowData.add(field);
    }

    public List<Field> getAllFieldsOfRow() {
        return this.rowData;
    }

    @Override
    public String toString() {
        return "Row [rowData=" + rowData + "]";
    }

    public Field getFieldByColumnName(String columnName) {
        Optional<Field> field = rowData.stream().filter(f -> f.getColumn().getName().equalsIgnoreCase(columnName)).findAny();
        if (field.isPresent()) {
            return field.get();
        }
        else {
            return null;
        }
    }

}
