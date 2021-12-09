package com.example;

import com.example.models.enums.Lock;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TableLock {

    private String transactionId;

    private String table;

    private String database;

    private Lock lock;
}
