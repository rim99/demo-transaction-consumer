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
    public List<AggregationItem> getRecordDuringTimeRange(LocalDateTime from, LocalDateTime to, AggregationTimeFrame timeframe, AggregationType aggregationType, List<PaymentType> payments) {
        return payments.stream().map(pt -> {
            RedisZsetIndex start = RedisZsetIndex.create(pt, OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
            RedisZsetIndex stop = RedisZsetIndex.create(pt, OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
            return redisStorage.getAll(start, stop);
        }).collect(LinkedList::new, List::addAll, List::addAll);
    }

    @Override
    public List<AggregationItem> getRecordsByPaymentVendor(LocalDateTime from, LocalDateTime to, AggregationTimeFrame timeframe, AggregationType aggregationType, PaymentVendor vendor, List<PaymentType> payments) {
        return payments.stream().map(pt -> {
            List<AggregationItem> tmpResult = new LinkedList<>();
            if (pt.equals(PaymentType.PURCHASE)) {
                RedisZsetIndex start = RedisZsetIndex.createForPurchase(
                        vendor, OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
                RedisZsetIndex stop = RedisZsetIndex.createForPurchase(
                        vendor, OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
                tmpResult.addAll(redisStorage.getAll(start, stop));
            }
            if (pt.equals(PaymentType.REFUND)) {
                RedisZsetIndex start = RedisZsetIndex.createForRefund(
                        vendor, true, OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
                RedisZsetIndex stop = RedisZsetIndex.createForRefund(
                        vendor, true, OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
                tmpResult.addAll(redisStorage.getAll(start, stop));
                start = RedisZsetIndex.createForRefund(
                        vendor, false, OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
                stop = RedisZsetIndex.createForRefund(
                        vendor, false, OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
                tmpResult.addAll(redisStorage.getAll(start, stop));
            }
            return tmpResult;
        }).collect(LinkedList::new, List::addAll, List::addAll);
    }

    @Override
    public List<AggregationItem> getRecordsByMerchant(LocalDateTime from, LocalDateTime to, AggregationTimeFrame timeframe, AggregationType aggregationType, Merchant merchant, List<PaymentType> payments) {
        return payments.stream().map(pt -> {
            List<AggregationItem> tmpResult = new LinkedList<>();
            if (pt.equals(PaymentType.PURCHASE)) {
                RedisZsetIndex start = RedisZsetIndex.createForPurchase(
                        merchant, OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
                RedisZsetIndex stop = RedisZsetIndex.createForPurchase(
                        merchant, OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
                tmpResult.addAll(redisStorage.getAll(start, stop));
            }
            if (pt.equals(PaymentType.REFUND)) {
                RedisZsetIndex start = RedisZsetIndex.createForRefund(
                        merchant, true, OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
                RedisZsetIndex stop = RedisZsetIndex.createForRefund(
                        merchant, true, OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
                tmpResult.addAll(redisStorage.getAll(start, stop));
                start = RedisZsetIndex.createForRefund(
                        merchant, false, OffsetDateTime.of(from, ZoneOffset.UTC), timeframe, aggregationType);
                stop = RedisZsetIndex.createForRefund(
                        merchant, false, OffsetDateTime.of(to, ZoneOffset.UTC), timeframe, aggregationType);
                tmpResult.addAll(redisStorage.getAll(start, stop));
            }
            return tmpResult;
        }).collect(LinkedList::new, List::addAll, List::addAll);
    }
}
