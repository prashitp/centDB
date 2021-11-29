package com.example.services.dataAccessor;

import com.example.models.Condition;
import com.example.models.Field;

public interface IOperandProcessor {

//    This method should process the operator of the field
//    And should return a boolean value whether it is true or not
    void processConditionOnTheField(Field field, Condition condition);
}
