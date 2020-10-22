package org.example.transaction.consumer.port;

import javax.inject.Singleton;

@Singleton
public interface TransactionRecordRepository {
    void save(TransactionRecord record);
}
