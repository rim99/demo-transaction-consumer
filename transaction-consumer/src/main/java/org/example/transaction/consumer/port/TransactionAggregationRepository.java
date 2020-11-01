package org.example.transaction.consumer.port;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
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
	List<AggregationItem> getRecordDuringTimeRange(LocalDateTime from, LocalDateTime to, AggregationTimeFrame timeframe, AggregationType aggregationType, List<PaymentType> payments);
    List<AggregationItem> getRecordsByPaymentVendor(LocalDateTime from, LocalDateTime to, AggregationTimeFrame timeframe, AggregationType aggregationType, PaymentVendor vendor, List<PaymentType> payments);
    List<AggregationItem> getRecordsByMerchant(LocalDateTime from, LocalDateTime to, AggregationTimeFrame timeframe, AggregationType aggregationType, Merchant merchant, List<PaymentType> payments);
}
