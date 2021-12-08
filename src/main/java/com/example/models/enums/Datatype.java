package com.example.models.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Datatype {
    VARCHAR, INTEGER, DATETIME;

    public static List<String> getAllDatatypes() {
        return Arrays.stream(Datatype.values()).map(Datatype::name).collect(Collectors.toList());
    }
}
