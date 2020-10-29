package org.example.transaction.consumer.port;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TransactionAggregationRepository {
	CompletableFuture<Void> updatePurchaseAmount(PaymentVendor vendor, double amount, OffsetDateTime datetime, AggregationTimeFrame timeframe);
	CompletableFuture<Void> updatePurchaseAmount(Merchant Merchant, double amount, OffsetDateTime datetime, AggregationTimeFrame timeframe);
	CompletableFuture<Void> updatePurchaseCount(PaymentVendor vendor, OffsetDateTime datetime, AggregationTimeFrame timeframe);
	CompletableFuture<Void> updatePurchaseCount(Merchant merchant, OffsetDateTime datetime, AggregationTimeFrame timeframe);
	CompletableFuture<Void> updateRefundAmount(PaymentVendor vendor, double amount, OffsetDateTime datetime, Boolean isValid, AggregationTimeFrame timeframe);
	CompletableFuture<Void> updateRefundAmount(Merchant merchant, double amount, OffsetDateTime dateTime, Boolean isValid, AggregationTimeFrame timeframe);
	CompletableFuture<Void> updateRefundCount(PaymentVendor vendor, OffsetDateTime datetime, Boolean isValid, AggregationTimeFrame timeframe);
	CompletableFuture<Void> updateRefundCount(Merchant merchant, OffsetDateTime datetime, Boolean isValid, AggregationTimeFrame timeframe);
	CompletableFuture<Void> updateAmountForType(PaymentType type, double amount, OffsetDateTime datetime, AggregationTimeFrame timeframe);
	CompletableFuture<Void> updateCountForType(PaymentType type, OffsetDateTime datetime, AggregationTimeFrame timeframe);
	List<AggregationItem> retrieveAggregations(LocalDateTime from, LocalDateTime to,
											   AggregationTimeFrame timeframe, AggregationType aggregationType,
											   Optional<Merchant> merchant, Optional<PaymentType> transactionType,
											   Optional<PaymentVendor> vendor);
}
