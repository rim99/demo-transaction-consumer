package org.example.transaction.consumer.port;

import java.util.concurrent.CompletableFuture;

public interface TransactionRecordRepository {
    CompletableFuture<Void> save(TransactionRecord record);
}
