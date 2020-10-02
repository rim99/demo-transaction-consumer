package org.example.transaction.consumer.service;

import java.util.function.Consumer;

import org.example.transaction.consumer.port.TransactionRecordRepository;
import org.example.transaction.consumer.port.TransactionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionRecordConsumeService implements Consumer<TransactionRecord> {

    private static final Logger logger = LoggerFactory.getLogger(TransactionRecordConsumeService.class);

    private TransactionRecordRepository transactionMessageRepository;
    private TransactionAggregationService transactionAggregationService;

    public TransactionRecordConsumeService(
            TransactionRecordRepository transactionMessageRepository,
            TransactionAggregationService transactionAggregationService
    ) {
        this.transactionMessageRepository = transactionMessageRepository;
        this.transactionAggregationService = transactionAggregationService;
    }

    @Override
    public void accept(TransactionRecord record) {
        System.out.println("[PRINTing] received one message: " + record);
        logger.info("received one message: " + record);
        transactionMessageRepository.save(record);
        transactionAggregationService.aggregate(record);
    }
}
