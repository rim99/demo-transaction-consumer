package org.example.transaction.consumer.service;

import java.util.function.Consumer;

import org.example.transaction.consumer.port.TransactionMessageRepository;
import org.example.transaction.consumer.port.TransactionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionMessageConsumeService implements Consumer<TransactionRecord> {

    private static final Logger logger = LoggerFactory.getLogger(TransactionMessageConsumeService.class);

    private TransactionMessageRepository transactionMessageRepository;

    public TransactionMessageConsumeService(TransactionMessageRepository transactionMessageRepository) {
        this.transactionMessageRepository = transactionMessageRepository;
    }

    @Override
    public void accept(TransactionRecord txnMsg) {
        System.out.println("[PRINTing] received one message: " + txnMsg);
        logger.info("received one message: " + txnMsg);
        transactionMessageRepository.save(txnMsg);
    }
}
