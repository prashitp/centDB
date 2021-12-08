package com.example.models.context;

import com.example.models.TransactionMessage;
import java.util.ArrayDeque;

public final class TransactionQueue {

    private static ArrayDeque<TransactionMessage> transactionMessages;

    public static Boolean add(TransactionMessage transactionMessage) {
        return TransactionQueue.transactionMessages.add(transactionMessage);
    }

    public static TransactionMessage peek() {
        return transactionMessages.peek();
    }

    public static TransactionMessage poll() {
        return transactionMessages.poll();
    }

    public static Boolean isEmpty() {
        return transactionMessages.isEmpty();
    }

}
