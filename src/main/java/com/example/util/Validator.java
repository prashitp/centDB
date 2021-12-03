package com.example.util;

public class Validator {

    public static Boolean isAlphaNumeric(String string) {
        return !string.trim().isEmpty() && string.matches("[A-Za-z0-9]+");
    }
}
