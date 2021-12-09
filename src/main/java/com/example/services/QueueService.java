package com.example.services;

import com.example.TableLock;
import com.example.models.TransactionMessage;
import com.example.models.enums.Lock;
import com.example.models.enums.Operation;
import com.example.util.QueryUtil;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.util.Constants.PIPE_DELIMITER;

public class QueueService {

    FileWriter queueFileWriter;
    FileWriter lockFileWriter;

    String queuePath = "storage/transaction/queue.txt";
    String lockPath = "storage/transaction/locks.txt";

    @SneakyThrows
    public QueueService() {
        queueFileWriter = new FileWriter(queuePath, true);
        lockFileWriter = new FileWriter(lockPath, true);
    }

    @SneakyThrows
    public List<TransactionMessage> getTransactions(String database) {
        List<TransactionMessage> transactionMessages = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(queuePath));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] strings = line.split("\\|");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(strings[0]);
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

            transactionMessages.add(TransactionMessage.builder()
                    .timestamp(timestamp)
                    .id(strings[1])
                    .database(strings[2])
                    .query(strings[3])
                    .tableQuery(QueryUtil.getQuery(strings[3], database))
                    .build());

            transactionMessages.sort(Comparator.comparing(TransactionMessage::getTimestamp));
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

    @SneakyThrows
    public Boolean save(TableLock tableLock) {
        Boolean isSaved = true;
        String currentTimestamp = new Timestamp(System.currentTimeMillis()).toString();
        String lockString = currentTimestamp.concat(PIPE_DELIMITER)
                .concat(tableLock.getTransactionId()).concat(PIPE_DELIMITER)
                .concat(tableLock.getDatabase()).concat(PIPE_DELIMITER)
                .concat(tableLock.getTable()).concat(PIPE_DELIMITER)
                .concat(tableLock.getLock().name());

        lockFileWriter.write(lockString.concat("\n"));
        lockFileWriter.flush();

        return isSaved;
    }


    @SneakyThrows
    public Set<TableLock> getLocks() {
        Set<TableLock> tableLocks = new HashSet<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(lockPath));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] strings = line.split("\\|");
            tableLocks.add(TableLock.builder()
                    .transactionId(strings[1])
                    .database(strings[2])
                    .table(strings[3])
                    .lock(Lock.valueOf(strings[4].trim()))
                    .build());
        }
        return tableLocks;
    }

    @SneakyThrows
    public void flush(String transactionId) {
        File file = new File(lockPath);
        List<String> out = Files.lines(file.toPath())
                .filter(line -> !line.contains(transactionId))
                .collect(Collectors.toList());
        Files.write(file.toPath(), out, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public Set<TableLock> getLocks(List<TransactionMessage> transactionMessages) {
        Set<TableLock> tableLocks = new HashSet<>();
        List<Operation> dmlOperations = Arrays.asList(Operation.INSERT, Operation.UPDATE,
                Operation.DELETE);
        List<Operation> ddlOperations = Arrays.asList(Operation.SELECT);

        List<TransactionMessage> dmlTransactions = transactionMessages.stream()
                .filter(data -> dmlOperations.contains(data.getTableQuery().getTableOperation()))
                .collect(Collectors.toList());
        List<TransactionMessage> ddlTransactions = transactionMessages.stream()
                .filter(data -> ddlOperations.contains(data.getTableQuery().getTableOperation()))
                .collect(Collectors.toList());

        for (TransactionMessage message: dmlTransactions) {
            tableLocks.add(TableLock.builder()
                    .transactionId(message.getId())
                    .lock(Lock.EXCLUSIVE)
                    .table(message.getTableQuery().getTableName())
                    .database(message.getTableQuery().getSchemaName())
                    .build());
        }

        for (TransactionMessage message: ddlTransactions) {
            tableLocks.add(TableLock.builder()
                    .transactionId(message.getId())
                    .lock(Lock.SHARED)
                    .table(message.getTableQuery().getTableName())
                    .database(message.getTableQuery().getSchemaName())
                    .build());
        }
        return tableLocks;
    }

    public void commit(String transactionId, String databaseName) {

        List<TransactionMessage> transactions = getTransactions(databaseName).stream()
                .filter(data -> data.getId().equals(transactionId))
                .collect(Collectors.toList());

        Set<TableLock> requiredLocks = getLocks(transactions);
        Set<TableLock> currentLocks = getLocks();

        if (isLocked(requiredLocks, currentLocks, databaseName)) {
            System.out.println("Waiting for lock");
        } else {
            for (TableLock requiredLock: requiredLocks) {
                save(requiredLock);
            }

            for (TransactionMessage transaction: transactions) {
                QueryUtil.execute(transaction.getQuery(), transaction.getDatabase());
            }

            flush(transactionId);
        }
    }

    public Boolean isLocked(Set<TableLock> requiredLocks, Set<TableLock> currentLocks, String database) {
        boolean isLocked = false;
        Map<String, TableLock> currentLockMap = new HashMap<>();
        currentLocks = currentLocks.stream()
                .filter(data -> data.getDatabase().equals(database))
                .collect(Collectors.toSet());
        for (TableLock lock: currentLocks) {
            currentLockMap.put(lock.getTable(), lock);
        }

        for (TableLock requiredLock: requiredLocks) {
            if (currentLockMap.containsKey(requiredLock.getTable())) {
                TableLock currentLock = currentLockMap.get(requiredLock.getTable());
                if ((requiredLock.getLock().equals(Lock.EXCLUSIVE) ||
                        (currentLock.getLock().equals(requiredLock.getLock())))) {
                    isLocked = true;
                    break;
                }
            }
        }
        return isLocked;
    }
}
