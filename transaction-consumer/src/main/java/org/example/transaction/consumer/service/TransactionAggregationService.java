package org.example.transaction.consumer.service;

import org.example.transaction.consumer.port.*;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TransactionAggregationService {

    private TransactionAggregationRepository transactionAggregationRepository;

    @Inject
    public TransactionAggregationService(TransactionAggregationRepository transactionAggregationRepository) {
        this.transactionAggregationRepository = transactionAggregationRepository;
    }

    public CompletableFuture<Void> aggregate(TransactionRecord r) {
        List<CompletableFuture<Void>> result = new LinkedList<>();

        AggregationTimeFrame.getAll().forEach(timeframe -> {
            result.add(transactionAggregationRepository.updateAmountForType(
                    r.getType(), r.getAmount(), r.getDatetime(), timeframe));
            result.add(transactionAggregationRepository.updateCountForType(
                    r.getType(), r.getDatetime(), timeframe));
        });

        Merchant m = new Merchant(r.getMid());

        if (PaymentType.PURCHASE.equals(r.getType())) {
            AggregationTimeFrame.getAll().forEach(timeframe -> {
                result.add(transactionAggregationRepository.updatePurchaseAmount(
                        r.getVendor(), r.getAmount(), r.getDatetime(), timeframe));
                result.add(transactionAggregationRepository.updatePurchaseAmount(
                        m, r.getAmount(), r.getDatetime(), timeframe));
                result.add(transactionAggregationRepository.updatePurchaseCount(
                        r.getVendor(), r.getDatetime(), timeframe));
                result.add(transactionAggregationRepository.updatePurchaseCount(
                        m, r.getDatetime(), timeframe));
            });
        }

        if (PaymentType.REFUND.equals(r.getType())) {
            Boolean isValid = r.getValid().orElseThrow();
            AggregationTimeFrame.getAll().forEach(timeframe -> {
                result.add(transactionAggregationRepository.updateRefundAmount(
                        r.getVendor(), r.getAmount(), r.getDatetime(), isValid, timeframe));
                result.add(transactionAggregationRepository.updateRefundAmount(
                        m, r.getAmount(), r.getDatetime(), isValid, timeframe));
                result.add(transactionAggregationRepository.updateRefundCount(
                        r.getVendor(), r.getDatetime(), isValid, timeframe));
                result.add(transactionAggregationRepository.updateRefundCount(
                        m, r.getDatetime(), isValid, timeframe));
            });
        }

        CompletableFuture[] f = new CompletableFuture[result.size()];
        return CompletableFuture.allOf(result.toArray(f));
    }
}
