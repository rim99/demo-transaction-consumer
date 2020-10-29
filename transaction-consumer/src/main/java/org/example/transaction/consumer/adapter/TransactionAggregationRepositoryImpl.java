package org.example.transaction.consumer.adapter;

import org.example.transaction.consumer.adapter.redis.RedisStorage;
import org.example.transaction.consumer.adapter.redis.RedisZsetIndex;
import org.example.transaction.consumer.port.*;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TransactionAggregationRepositoryImpl implements TransactionAggregationRepository {

    private RedisStorage redisStorage;

    @Inject
    public TransactionAggregationRepositoryImpl(RedisStorage redisStorage) {
        this.redisStorage = redisStorage;
    }

    @Override
    public CompletableFuture<Void> updatePurchaseAmount(PaymentVendor vendor, double amount, OffsetDateTime datetime, AggregationTimeFrame timeframe) {
        CompletableFuture<Void> r = new CompletableFuture<>();
        redisStorage.add(RedisZsetIndex.createForPurchase(vendor, datetime, timeframe, AggregationType.AMOUNT), amount)
                .thenApply(m -> r.complete(null));
        return r;
    }

    @Override
    public CompletableFuture<Void> updatePurchaseAmount(Merchant merchant, double amount, OffsetDateTime datetime, AggregationTimeFrame timeframe) {
        CompletableFuture<Void> r = new CompletableFuture<>();
        redisStorage.add(RedisZsetIndex.createForPurchase(merchant, datetime, timeframe, AggregationType.AMOUNT), amount)
                .thenApply(m -> r.complete(null));
        return r;
    }

    @Override
    public CompletableFuture<Void> updatePurchaseCount(PaymentVendor vendor, OffsetDateTime datetime, AggregationTimeFrame timeframe) {
        CompletableFuture<Void> r = new CompletableFuture<>();
        redisStorage.addOne(RedisZsetIndex.createForPurchase(vendor, datetime, timeframe, AggregationType.COUNT))
                .thenApply(m -> r.complete(null));
        return r;
    }

    @Override
    public CompletableFuture<Void> updatePurchaseCount(Merchant merchant, OffsetDateTime datetime, AggregationTimeFrame timeframe) {
        CompletableFuture<Void> r = new CompletableFuture<>();
        redisStorage.addOne(RedisZsetIndex.createForPurchase(merchant, datetime, timeframe, AggregationType.COUNT))
                .thenApply(m -> r.complete(null));
        return r;
    }

    @Override
    public CompletableFuture<Void> updateRefundAmount(PaymentVendor vendor, double amount, OffsetDateTime datetime, Boolean isValid, AggregationTimeFrame timeframe) {
        CompletableFuture<Void> r = new CompletableFuture<>();
        redisStorage.add(RedisZsetIndex.createForRefund(vendor, isValid, datetime, timeframe, AggregationType.AMOUNT), amount)
                .thenApply(m -> r.complete(null));
        return r;
    }

    @Override
    public CompletableFuture<Void> updateRefundAmount(Merchant merchant, double amount, OffsetDateTime dateTime, Boolean isValid, AggregationTimeFrame timeframe) {
        CompletableFuture<Void> r = new CompletableFuture<>();
        redisStorage.add(RedisZsetIndex.createForRefund(merchant, isValid, dateTime, timeframe, AggregationType.AMOUNT), amount)
                .thenApply(m -> r.complete(null));
        return r;
    }

    @Override
    public CompletableFuture<Void> updateRefundCount(PaymentVendor vendor, OffsetDateTime datetime, Boolean isValid, AggregationTimeFrame timeframe) {
        CompletableFuture<Void> r = new CompletableFuture<>();
        redisStorage.addOne(RedisZsetIndex.createForRefund(vendor, isValid, datetime, timeframe, AggregationType.COUNT))
                .thenApply(m -> r.complete(null));
        return r;
    }

    @Override
    public CompletableFuture<Void> updateRefundCount(Merchant merchant, OffsetDateTime datetime, Boolean isValid, AggregationTimeFrame timeframe) {
        CompletableFuture<Void> r = new CompletableFuture<>();
        redisStorage.addOne(RedisZsetIndex.createForRefund(merchant, isValid, datetime, timeframe, AggregationType.COUNT))
                .thenApply(m -> r.complete(null));
        return r;
    }

    @Override
    public CompletableFuture<Void> updateAmountForType(PaymentType type, double amount, OffsetDateTime datetime, AggregationTimeFrame timeframe) {
        CompletableFuture<Void> r = new CompletableFuture<>();
        redisStorage.add(RedisZsetIndex.create(type, datetime, timeframe, AggregationType.AMOUNT), amount)
                .thenApply(m -> r.complete(null));
        return r;
    }

    @Override
    public CompletableFuture<Void> updateCountForType(PaymentType type, OffsetDateTime datetime, AggregationTimeFrame timeframe) {
        CompletableFuture<Void> r = new CompletableFuture<>();
        redisStorage.addOne(RedisZsetIndex.create(type, datetime, timeframe, AggregationType.COUNT))
                .thenApply(m -> r.complete(null));
        return r;
    }

    @Override
    public List<AggregationItem> retrieveAggregations(
            LocalDateTime from, LocalDateTime to, AggregationTimeFrame timeframe, AggregationType aggregationType,
            Optional<Merchant> merchant, Optional<PaymentType> transactionType, Optional<PaymentVendor> vendor) {

        List<PaymentType> payments = new ArrayList<>(2);
        transactionType.ifPresentOrElse(payments::add, () -> {
            payments.add(PaymentType.PURCHASE);
            payments.add(PaymentType.REFUND);
        });
        List<AggregationItem> result = new LinkedList<>();

        if (merchant.isPresent()) {
            payments.forEach(pt -> {
                if (pt.equals(PaymentType.PURCHASE)) {
                    RedisZsetIndex start = RedisZsetIndex.createForPurchase(
                            merchant.get(), OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
                    RedisZsetIndex stop = RedisZsetIndex.createForPurchase(
                            merchant.get(), OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
                    result.addAll(redisStorage.getAll(start, stop));
                }
                if (pt.equals(PaymentType.REFUND)) {
                    RedisZsetIndex start = RedisZsetIndex.createForRefund(
                            merchant.get(), true, OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
                    RedisZsetIndex stop = RedisZsetIndex.createForRefund(
                            merchant.get(), true, OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
                    result.addAll(redisStorage.getAll(start, stop));
                    start = RedisZsetIndex.createForRefund(
                            merchant.get(), false, OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
                    stop = RedisZsetIndex.createForRefund(
                            merchant.get(), false, OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
                    result.addAll(redisStorage.getAll(start, stop));
                }
            });
        }

        if (result.isEmpty() && vendor.isPresent()) {
            payments.forEach(pt -> {
                if (pt.equals(PaymentType.PURCHASE)) {
                    RedisZsetIndex start = RedisZsetIndex.createForPurchase(
                            vendor.get(), OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
                    RedisZsetIndex stop = RedisZsetIndex.createForPurchase(
                            vendor.get(), OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
                    result.addAll(redisStorage.getAll(start, stop));
                }
                if (pt.equals(PaymentType.REFUND)) {
                    RedisZsetIndex start = RedisZsetIndex.createForRefund(
                            vendor.get(), true, OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
                    RedisZsetIndex stop = RedisZsetIndex.createForRefund(
                            vendor.get(), true, OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
                    result.addAll(redisStorage.getAll(start, stop));
                    start = RedisZsetIndex.createForRefund(
                            vendor.get(), false, OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
                    stop = RedisZsetIndex.createForRefund(
                            vendor.get(), false, OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
                    result.addAll(redisStorage.getAll(start, stop));
                }
            });
        }

        if (result.isEmpty()) {
            payments.forEach(pt -> {
                RedisZsetIndex start = RedisZsetIndex.create(pt, OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
                RedisZsetIndex stop = RedisZsetIndex.create(pt, OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
                result.addAll(redisStorage.getAll(start, stop));
            });
        }

        Collections.sort(result);
        return result;
    }
}
