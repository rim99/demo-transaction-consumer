package org.example.transaction.consumer.service;

import org.example.transaction.consumer.port.*;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TransactionAggregationHttpServiceImpl implements TransactionAggregationHttpService {

    private TransactionAggregationRepository transactionAggregationRepository;

    @Inject
    public TransactionAggregationHttpServiceImpl(
            TransactionAggregationRepository transactionAggregationRepository
    ) {
        this.transactionAggregationRepository = transactionAggregationRepository;
    }

    @Override
    public List<AggregationItem> getAggregations(LocalDateTime from,
                                                 LocalDateTime to,
                                                 AggregationTimeFrame timeframe,
                                                 AggregationType type,
                                                 Optional<Merchant> merchant,
                                                 Optional<PaymentType> transactionType,
                                                 Optional<PaymentVendor> vendor
    ) {
        return this.transactionAggregationRepository.retrieveAggregations(
                from, to, timeframe, type, merchant, transactionType, vendor);
    }
}
