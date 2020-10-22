package org.example.transaction.consumer.config;

import dagger.Component;
import org.example.transaction.consumer.adapter.TransactionAggregationHttpAPI;
import org.example.transaction.consumer.service.TransactionRecordConsumeService;

import javax.inject.Singleton;

@Singleton
@Component(modules = Base.class)
public interface Module {
    TransactionRecordConsumeService transactionRecordConsumeService();
    TransactionAggregationHttpAPI transactionAggregationHttpAPI();
}
