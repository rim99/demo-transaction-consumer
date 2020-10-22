package org.example.transaction.consumer.adapter.redis;

import org.example.transaction.consumer.port.*;

import java.time.OffsetDateTime;

public class RedisZsetIndex {
    String zsetName;
    String memberName;
    double score;

    private RedisZsetIndex(String zsetName, String memberName, double score) {
        this.zsetName = zsetName;
        this.memberName = memberName;
        this.score = score;
    }

    public String getZsetName() {
        return zsetName;
    }

    public String getMemberName() {
        return memberName;
    }

    public double getScore() {
        return score;
    }

    public static RedisZsetIndex createForPurchase(PaymentVendor vendor,
                                    OffsetDateTime datetime, AggregationTimeFrame timeFrame,
                                    AggregationType type) {
        String zsetName = PaymentType.PURCHASE.name() + vendor.getLabel() +
                timeFrame.name() +
                type.name();
        String memberName = Helper.getTime(datetime, timeFrame);
        double score = datetime.toEpochSecond();
        return new RedisZsetIndex(zsetName, memberName, score);
    }

    public static RedisZsetIndex createForPurchase(Merchant merchant,
                                    OffsetDateTime datetime, AggregationTimeFrame timeFrame,
                                    AggregationType type) {
        String zsetName = PaymentType.PURCHASE.name() +
                timeFrame.name() +
                type.name() +
                "MERCHANT";
        String memberName = Helper.getTime(datetime, timeFrame);
        double score = calculateScore(merchant.getId(), datetime.toEpochSecond());
        return new RedisZsetIndex(zsetName, memberName, score);
    }

    public static RedisZsetIndex createForRefund(PaymentVendor vendor, Boolean isValid,
                                  OffsetDateTime datetime, AggregationTimeFrame timeFrame,
                                  AggregationType type) {
        String zsetName = PaymentType.REFUND.name() + vendor.getLabel() +
                (isValid ? "valid" : "invalid") +
                timeFrame.name() +
                type.name();
        String memberName = Helper.getTime(datetime, timeFrame);
        double score = datetime.toEpochSecond();
        return new RedisZsetIndex(zsetName, memberName, score);
    }

    public static RedisZsetIndex createForRefund(Merchant merchant, Boolean isValid,
                                  OffsetDateTime datetime, AggregationTimeFrame timeFrame,
                                  AggregationType type) {
        String zsetName = PaymentType.REFUND.name() +
                (isValid ? "valid" : "invalid") +
                timeFrame.name() +
                type.name() +
                "MERCHANT";
        String memberName = merchant.getId() + "_" + Helper.getTime(datetime, timeFrame);
        double score = calculateScore(merchant.getId(), datetime.toEpochSecond());
        return new RedisZsetIndex(zsetName, memberName, score);
    }

    public static RedisZsetIndex create(PaymentType paymentType,
                                        OffsetDateTime datetime, AggregationTimeFrame timeFrame,
                                        AggregationType aggregationType) {
        String zsetName = paymentType.getLabel() + timeFrame.name() + aggregationType.name();
        String memberName = Helper.getTime(datetime, timeFrame);
        double score = datetime.toEpochSecond();
        return new RedisZsetIndex(zsetName, memberName, score);
    }

    static double calculateScore(int id, long epochSec) {
        // make sure the score will not be repeated in one day, aka 86,400 sec
        double standard = 1000_000D;
        return id * standard + epochSec;
    }
}
