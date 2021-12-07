package com.example.models;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Builder
public class Row {

    private List<Field> fields;

    public Row() {
        fields = new ArrayList<Field>();
    }

    public Row(Row row) {
        this.fields = new ArrayList<>(row.getAllFieldsOfRow());
    }

    public Row(List<Field> fields) {
        super();
        this.fields = fields;
    }

    public void addField(Field field) {
        this.fields.add(field);
    }

    public List<Field> getAllFieldsOfRow() {
        return this.fields;
    }

    @Override
    public String toString() {
        return "Row [rowData=" + fields + "]";
    }

    public Field getFieldByColumnName(String columnName) {
        Optional<Field> field = fields.stream().filter(f -> f.getColumn().getName().equalsIgnoreCase(columnName)).findAny();
        if (field.isPresent()) {
            return field.get();
        }
        else {
            return null;
        }
    }

}
