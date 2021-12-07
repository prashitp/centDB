package com.example.models;

import com.example.models.enums.Operator;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class Condition {

    String operand1;

    Operator operator;

    String operand2;

}
