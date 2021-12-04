package com.example.models.enums;

import com.example.exceptions.UnrecognisedOperator;

public enum Operator {
    EQUALS("="),
    LESS_THAN("<"),
    GREATER_THAN(">");

    public final String operatorValue;

    Operator(String operatorValue) {
        this.operatorValue = operatorValue;
    }

    public static Operator getOperatorType(String operatorValue) throws UnrecognisedOperator {
        Operator operator;
        try {
            operator = Operator.valueOf(operatorValue);
        }
        catch (IllegalArgumentException exception) {
            throw new UnrecognisedOperator("Operator " + operatorValue + " is not supported");
        }
        return operator;
    }

}

