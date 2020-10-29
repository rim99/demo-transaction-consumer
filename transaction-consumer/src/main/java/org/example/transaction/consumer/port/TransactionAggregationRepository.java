package org.example.transaction.consumer.port;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionAggregationRepository {
	void updatePurchaseAmount(PaymentVendor vendor, double amount, OffsetDateTime datetime, AggregationTimeFrame timeframe);
	void updatePurchaseAmount(Merchant Merchant, double amount, OffsetDateTime datetime, AggregationTimeFrame timeframe);
	void updatePurchaseCount(PaymentVendor vendor, OffsetDateTime datetime, AggregationTimeFrame timeframe);
	void updatePurchaseCount(Merchant merchant, OffsetDateTime datetime, AggregationTimeFrame timeframe);
	void updateRefundAmount(PaymentVendor vendor, double amount, OffsetDateTime datetime, Boolean isValid, AggregationTimeFrame timeframe);
	void updateRefundAmount(Merchant merchant, double amount, OffsetDateTime dateTime, Boolean isValid, AggregationTimeFrame timeframe);
	void updateRefundCount(PaymentVendor vendor, OffsetDateTime datetime, Boolean isValid, AggregationTimeFrame timeframe);
	void updateRefundCount(Merchant merchant, OffsetDateTime datetime, Boolean isValid, AggregationTimeFrame timeframe);
	void updateAmountForType(PaymentType type, double amount, OffsetDateTime datetime, AggregationTimeFrame timeframe);
	void updateCountForType(PaymentType type, OffsetDateTime datetime, AggregationTimeFrame timeframe);
	List<AggregationItem> retrieveAggregations(LocalDateTime from, LocalDateTime to,
											   AggregationTimeFrame timeframe, AggregationType aggregationType,
											   Optional<Merchant> merchant, Optional<PaymentType> transactionType,
											   Optional<PaymentVendor> vendor);
}
