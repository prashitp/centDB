package com.example.models.context;

import com.example.models.Transaction;
import java.util.ArrayDeque;

public final class TransactionQueue {

    private static ArrayDeque<Transaction> transactions;

    public static Boolean add(Transaction transaction) {
        return TransactionQueue.transactions.add(transaction);
    }

    public static Transaction peek() {
        return transactions.peek();
    }

    public static Transaction poll() {
        return transactions.poll();
    }

    public static Boolean isEmpty() {
        return transactions.isEmpty();
    }

}
