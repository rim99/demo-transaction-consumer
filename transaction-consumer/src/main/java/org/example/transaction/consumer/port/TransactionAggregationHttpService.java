package org.example.transaction.consumer.port;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Singleton
public interface TransactionAggregationHttpService {

    List<AggregationItem> getAggregations(LocalDateTime from,
                                          LocalDateTime to,
                                          AggregationTimeFrame timeframe,
                                          AggregationType type,
                                          Optional<Merchant> merchant,
                                          Optional<PaymentType> transactionType,
                                          Optional<PaymentVendor> vendor);

}
