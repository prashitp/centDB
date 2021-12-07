package com.example.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static Boolean isAlphaNumeric(String string) {
        return !string.trim().isEmpty() && string.matches("[A-Za-z0-9]+");
    }

    public static String match(String string, String from, String to) {
        return find(string, from.concat("(.*?)").concat(to));
    }

    public static String matchFrom(String string, String from) {
        return find(string, from.concat("(.*)"));
    }

    public static String matchTo(String string, String to) {
        return string.split(to)[0].trim();
    }

    private static String find(String string, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        matcher.find();

        return matcher.group(1).trim();
    }
}
