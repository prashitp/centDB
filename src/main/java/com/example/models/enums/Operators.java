package com.example.models.enums;

import com.example.exceptions.UnrecognisedOperator;

public enum Operators {

    EQUALS("="),
    LESS_THAN("<"),
    GREATER_THAN(">");

    public final String operatorValue;

    Operators(String operatorValue) {
        this.operatorValue = operatorValue;
    }

    public static Operators getOperatorType(String operatorValue) throws UnrecognisedOperator {
        Operators operator;
        try {
            operator = Operators.valueOf(operatorValue);
        }
        catch (IllegalArgumentException exception) {
            throw new UnrecognisedOperator("Operator " + operatorValue + " is not supported");
        }
        return operator;
    }

}

