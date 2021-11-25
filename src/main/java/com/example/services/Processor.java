package com.example.services;

import com.example.models.DatabaseOperation;
import com.example.models.TableOperation;

public interface Processor {

    DatabaseOperation getDatabaseOperation(String query);

    TableOperation getTableOperation(String query);
}
