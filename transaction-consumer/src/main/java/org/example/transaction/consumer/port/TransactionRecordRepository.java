package org.example.transaction.consumer.port;

public interface TransactionRecordRepository {
    void save(TransactionRecord record);
}
