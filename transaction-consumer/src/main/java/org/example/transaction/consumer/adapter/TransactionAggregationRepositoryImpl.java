package org.example.transaction.consumer.adapter;

import org.example.transaction.consumer.adapter.redis.RedisStorage;
import org.example.transaction.consumer.adapter.redis.RedisZsetIndex;
import org.example.transaction.consumer.port.*;

import java.time.OffsetDateTime;

public class TransactionAggregationRepositoryImpl implements TransactionAggregationRepository {

    private RedisStorage redisStorage;

    public TransactionAggregationRepositoryImpl(RedisStorage redisStorage) {
        this.redisStorage = redisStorage;
    }

    @Override
    public void updatePurchaseAmount(PaymentVendor vendor, double amount, OffsetDateTime datetime, AggregationTimeFrame timeframe) {
        redisStorage.add(RedisZsetIndex.createForPurchase(vendor, datetime, timeframe, AggregationType.AMOUNT), amount);
    }

    @Override
    public void updatePurchaseAmount(Merchant merchant, double amount, OffsetDateTime datetime, AggregationTimeFrame timeframe) {
        redisStorage.add(RedisZsetIndex.createForPurchase(merchant, datetime, timeframe, AggregationType.AMOUNT), amount);
    }

    @Override
    public void updatePurchaseCount(PaymentVendor vendor, OffsetDateTime datetime, AggregationTimeFrame timeframe) {
        redisStorage.addOne(RedisZsetIndex.createForPurchase(vendor, datetime, timeframe, AggregationType.COUNT));
    }

    @Override
    public void updatePurchaseCount(Merchant merchant, OffsetDateTime datetime, AggregationTimeFrame timeframe) {
        redisStorage.addOne(RedisZsetIndex.createForPurchase(merchant, datetime, timeframe, AggregationType.COUNT));
    }

    @Override
    public void updateRefundAmount(PaymentVendor vendor, double amount, OffsetDateTime datetime, Boolean isValid, AggregationTimeFrame timeframe) {
        redisStorage.add(RedisZsetIndex.createForRefund(vendor, isValid, datetime, timeframe, AggregationType.AMOUNT), amount);
    }

    @Override
    public void updateRefundAmount(Merchant merchant, double amount, OffsetDateTime dateTime, Boolean isValid, AggregationTimeFrame timeframe) {
        redisStorage.add(RedisZsetIndex.createForRefund(merchant, isValid, dateTime, timeframe, AggregationType.AMOUNT), amount);
    }

    @Override
    public void updateRefundCount(PaymentVendor vendor, OffsetDateTime datetime, Boolean isValid, AggregationTimeFrame timeframe) {
        redisStorage.addOne(RedisZsetIndex.createForRefund(vendor, isValid, datetime, timeframe, AggregationType.COUNT));
    }

    @Override
    public void updateRefundCount(Merchant merchant, OffsetDateTime datetime, Boolean isValid, AggregationTimeFrame timeframe) {
        redisStorage.addOne(RedisZsetIndex.createForRefund(merchant, isValid, datetime, timeframe, AggregationType.COUNT));
    }

    @Override
    public void updateAmountForType(PaymentType type, double amount, OffsetDateTime datetime, AggregationTimeFrame timeframe) {
        redisStorage.add(RedisZsetIndex.create(type, datetime, timeframe, AggregationType.AMOUNT), amount);
    }

    @Override
    public void updateCountForType(PaymentType type, OffsetDateTime datetime, AggregationTimeFrame timeframe) {
        redisStorage.addOne(RedisZsetIndex.create(type, datetime, timeframe, AggregationType.COUNT));
    }
}
