package org.example.transaction.consumer.service;

import org.example.transaction.consumer.port.*;

import java.time.LocalDateTime;
import java.util.*;

public class TransactionAggregationHttpServiceImpl implements TransactionAggregationHttpService {

    @Override
    public List<AggregationItem> getAggregations(LocalDateTime from,
                                                 LocalDateTime to,
                                                 AggregationTimeFrame timeframe,
                                                 AggregationType type,
                                                 Optional<Merchant> merchant,
                                                 Optional<PaymentType> transactionType,
                                                 Optional<PaymentVendor> vendor
    ) {
        return new ArrayList<>(){{
            add(new AggregationItem("20200808T0516", 123.59D));
        }};
    }
}
