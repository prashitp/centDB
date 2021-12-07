package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForeignKey {

    private String foreignKeyColumn;

    private String referenceTableName;

    private String referenceColumnName;

}
