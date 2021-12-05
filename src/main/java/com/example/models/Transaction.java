package com.example.models;

import com.example.models.enums.Operation;

public class Transaction {

    private String id;

    private String number;

    private Integer priority;

    private TableQuery tableQuery;

    private Transaction previousTransaction;

    private Transaction nextTransaction;

    private Operation operation;

    private Table table;

    private Row beforeRow;

    private Row afterRow;

}
