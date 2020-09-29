package org.example.transaction.consumer.service;

import java.util.function.Consumer;

import org.example.transaction.consumer.entity.TransactionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionMessageConsumeService implements Consumer<TransactionMessage> {

    private static final Logger logger = LoggerFactory.getLogger(TransactionMessageConsumeService.class);

    @Override
    public void accept(TransactionMessage t) {
        System.out.println("[PRINTing] received one message: " + t);
        logger.info("received one message: " + t);
    }
}
