package org.example.transaction.consumer.service;

import org.example.transaction.consumer.port.TransactionRecord;
import org.example.transaction.consumer.port.TransactionRecordRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class TransactionRecordConsumeService implements Consumer<TransactionRecord> {

    private TransactionRecordRepository transactionMessageRepository;
    private TransactionAggregationService transactionAggregationService;

    @Inject
    public TransactionRecordConsumeService(
            TransactionRecordRepository transactionMessageRepository,
            TransactionAggregationService transactionAggregationService
    ) {
        this.transactionMessageRepository = transactionMessageRepository;
        this.transactionAggregationService = transactionAggregationService;
    }

    @Override
    public void accept(TransactionRecord record) {
        CompletableFuture<Void> finish = CompletableFuture.allOf(
                transactionMessageRepository.save(record),
                transactionAggregationService.aggregate(record)
        );

        boolean got = false;
        while (!got) {
            try {
                finish.get();
                got = true;
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Waiting for consume result of transaction message: " + record.getId());
            }
        }
    }
}
