package org.example.transaction.consumer.service;

import org.example.transaction.consumer.port.*;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;

public class TransactionAggregationHttpServiceImpl implements TransactionAggregationHttpService {

    private TransactionAggregationRepository transactionAggregationRepository;

    @Inject
    public TransactionAggregationHttpServiceImpl(TransactionAggregationRepository transactionAggregationRepository) {
        this.transactionAggregationRepository = transactionAggregationRepository;
    }

    @Override
    public List<AggregationItem> getAggregations(LocalDateTime from,
                                                 LocalDateTime to,
                                                 AggregationTimeFrame timeframe,
                                                 AggregationType aggregationType,
                                                 Optional<Merchant> merchant,
                                                 Optional<PaymentType> transactionType,
                                                 Optional<PaymentVendor> vendor
    ) {
        List<PaymentType> payments = new ArrayList<>(2);
        transactionType.ifPresentOrElse(payments::add, () -> {
            payments.add(PaymentType.PURCHASE);
            payments.add(PaymentType.REFUND);
        });
        List<AggregationItem> result = new LinkedList<>();

        if (merchant.isPresent()) {
            result = this.transactionAggregationRepository.getRecordsByMerchant(
                    from, to, timeframe, aggregationType, merchant.get(), payments);
        }

        if (result.isEmpty() && vendor.isPresent()) {
            result = this.transactionAggregationRepository.getRecordsByPaymentVendor(
                    from, to, timeframe, aggregationType, vendor.get(), payments);
        }

        if (result.isEmpty()) {
            result = this.transactionAggregationRepository.getRecordDuringTimeRange(
                    from, to, timeframe, aggregationType, payments);
        }

        Collections.sort(result);
        return result;
    }
}
