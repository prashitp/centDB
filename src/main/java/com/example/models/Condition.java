package com.example.models;

import com.example.models.enums.Operator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Condition {

    String operand1;

    String operand2;

    Operator operator;
}
