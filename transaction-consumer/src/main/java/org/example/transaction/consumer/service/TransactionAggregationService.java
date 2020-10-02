package org.example.transaction.consumer.service;

import org.example.transaction.consumer.port.*;

public class TransactionAggregationService {

    private TransactionAggregationRepository transactionAggregationRepository;

    public TransactionAggregationService(TransactionAggregationRepository transactionAggregationRepository) {
        this.transactionAggregationRepository = transactionAggregationRepository;
    }

    public void aggregate(TransactionRecord r) {
        AggregationTimeFrame.getAll().forEach(timeframe -> {
            transactionAggregationRepository.updateAmountForType(r.getType(), r.getAmount(), r.getDatetime(), timeframe);
            transactionAggregationRepository.updateCountForType(r.getType(), r.getDatetime(), timeframe);
        });

        Merchant m = new Merchant(r.getMid());

        if (PaymentType.PURCHASE.equals(r.getType())) {
            AggregationTimeFrame.getAll().forEach(timeframe -> {
                transactionAggregationRepository.updatePurchaseAmount(r.getVendor(), r.getAmount(), r.getDatetime(), timeframe);
                transactionAggregationRepository.updatePurchaseAmount(m, r.getAmount(), r.getDatetime(), timeframe);
                transactionAggregationRepository.updatePurchaseCount(r.getVendor(), r.getDatetime(), timeframe);
                transactionAggregationRepository.updatePurchaseCount(m, r.getDatetime(), timeframe);
            });
        }

        if (PaymentType.REFUND.equals(r.getType())) {
            Boolean isValid = r.getValid().orElseThrow();
            AggregationTimeFrame.getAll().forEach(timeframe -> {
                transactionAggregationRepository.updateRefundAmount(r.getVendor(), r.getAmount(), r.getDatetime(), isValid, timeframe);
                transactionAggregationRepository.updateRefundAmount(m, r.getAmount(), r.getDatetime(), isValid, timeframe);
                transactionAggregationRepository.updateRefundCount(r.getVendor(), r.getDatetime(), isValid, timeframe);
                transactionAggregationRepository.updateRefundCount(m, r.getDatetime(), isValid, timeframe);
            });
        }
    }
}
