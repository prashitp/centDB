package com.example.services.accessor;

import com.example.models.Condition;
import com.example.models.Field;

public interface OperandProcessor {

//    This method should process the operator of the field
//    And should return a boolean value whether it is true or not
    boolean process(Field field, Condition condition);
}
