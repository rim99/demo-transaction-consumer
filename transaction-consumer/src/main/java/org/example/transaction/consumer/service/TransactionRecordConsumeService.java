package org.example.transaction.consumer.service;

import org.example.transaction.consumer.port.TransactionRecord;
import org.example.transaction.consumer.port.TransactionRecordRepository;

import javax.inject.Inject;
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
        transactionMessageRepository.save(record);
        transactionAggregationService.aggregate(record);
    }
}
