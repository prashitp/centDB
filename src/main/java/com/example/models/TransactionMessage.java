package com.example.models;


import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class TransactionMessage {

    private String id;

    private Timestamp timestamp;

    private String database;

    private TableQuery tableQuery;

    private String query;

    private Row beforeRow;

    private Row afterRow;

}
