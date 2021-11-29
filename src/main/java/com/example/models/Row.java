package com.example.models;

import java.util.ArrayList;
import java.util.List;

public class Row {

    private List<Field> rowData;

    public Row() {
        rowData = new ArrayList<Field>();
    }

    public Row(Row row) {
        this.rowData = new ArrayList<>(row.getAllFieldsOfTheRow());
    }

    public Row(List<Field> rowData) {
        super();
        this.rowData = rowData;
    }

    public void addField(Field field) {
        this.rowData.add(field);
    }

    public List<Field> getAllFieldsOfTheRow() {
        return this.rowData;
    }

    @Override
    public String toString() {
        return "Row [rowData=" + rowData + "]";
    }

}
