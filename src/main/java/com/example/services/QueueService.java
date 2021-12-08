package com.example.services;

import com.example.models.TransactionMessage;
import com.example.models.enums.Lock;
import com.example.models.enums.Operation;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.util.Constants.PIPE_DELIMITER;

public class QueueService {

    FileWriter queueFileWriter;
    FileWriter lockFileWriter;

    String queuePath = "storage/transactions/queue.txt";
    String lockPath = "storage/transactions/locks.txt";

    @SneakyThrows
    public QueueService() {
        queueFileWriter = new FileWriter(queuePath, true);
        lockFileWriter = new FileWriter(lockPath, true);
    }

    @SneakyThrows
    public List<TransactionMessage> getTransactions() {
        List<TransactionMessage> transactionMessages = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(queuePath));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] strings = line.split("\\|");
            transactionMessages.add(TransactionMessage.builder()
                    .timestamp(Timestamp.valueOf(strings[0]))
                    .id(strings[1])
                    .database(strings[2])
                    .query(strings[3])
                    .build());
        }
        return transactionMessages;
    }

    @SneakyThrows
    public Boolean save(TransactionMessage transactionMessage) {
        Boolean isSaved = true;
        String transactionString = transactionMessage.getTimestamp().toString().concat(PIPE_DELIMITER)
                .concat(transactionMessage.getId()).concat(PIPE_DELIMITER)
                .concat(transactionMessage.getDatabase()).concat(PIPE_DELIMITER)
                .concat(transactionMessage.getQuery());

        queueFileWriter.write(transactionString.concat("\n"));
        queueFileWriter.flush();

        return isSaved;
    }

    public void execute() {

    }

    public void lock() {

    }

    public Lock getLock(List<TransactionMessage> transactionMessages) {
        Lock lock = Lock.SHARED;
        List<Operation> dmlOperations = Arrays.asList(Operation.INSERT, Operation.UPDATE, Operation.DELETE);
        Optional<TransactionMessage> dmlTransaction = transactionMessages.stream()
                .filter(data -> dmlOperations.contains(data.getTableQuery().getTableOperation()))
                .findAny();

        if (dmlTransaction.isPresent()) {
            lock = Lock.EXCLUSIVE;
        }
        return lock;
    }

    public void commit(TransactionMessage transactionMessage) {
        String transactionId = transactionMessage.getId();

        // Loop locks and remove the locks.
    }
}
