package org.example.transaction.consumer.port;

public interface TransactionMessageRepository {
    void save(TransactionRecord record);
}
