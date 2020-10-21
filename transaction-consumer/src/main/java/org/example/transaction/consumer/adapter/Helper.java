package org.example.transaction.consumer.adapter;


import org.example.transaction.consumer.port.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.time.temporal.ChronoField.*;

class Helper {
    static DateTimeFormatter minute;
    static DateTimeFormatter hour;
    static DateTimeFormatter day;

    static {
        minute = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendValue(YEAR, 4)
                .appendValue(MONTH_OF_YEAR, 2)
                .appendValue(DAY_OF_MONTH, 2)
                .optionalStart()
                .parseLenient()
                .appendOffset("+HHMM", "Z")
                .parseStrict()
                .toFormatter();
        hour = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendValue(YEAR, 4)
                .appendValue(MONTH_OF_YEAR, 2)
                .appendValue(DAY_OF_MONTH, 2)
                .optionalStart()
                .parseLenient()
                .appendOffset("+HH", "Z")
                .parseStrict()
                .toFormatter();
        day = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendValue(YEAR, 4)
                .appendValue(MONTH_OF_YEAR, 2)
                .appendValue(DAY_OF_MONTH, 2)
                .optionalStart()
                .parseStrict()
                .toFormatter();
    }

    static String getKeyForPurchase(PaymentVendor vendor,
                                    OffsetDateTime datetime, AggregationTimeFrame timeFrame,
                                    AggregationType type) {
        return PaymentType.PURCHASE.toString() +
                "_" +
                vendor.getLabel() +
                "_" +
                getTime(datetime, timeFrame) +
                "_" +
                timeFrame.toString() +
                "_" +
                type.toString();
    }

    static String getKeyForPurchase(Merchant merchant,
                                    OffsetDateTime datetime, AggregationTimeFrame timeFrame,
                                    AggregationType type) {
        return PaymentType.PURCHASE.toString() +
                "_" +
                merchant.getLabel() +
                "_" +
                getTime(datetime, timeFrame) +
                "_" +
                timeFrame.toString() +
                "_" +
                type.toString();
    }

    static String getKeyForRefund(PaymentVendor vendor, Boolean isValid,
                                  OffsetDateTime datetime, AggregationTimeFrame timeFrame,
                                  AggregationType type) {
        return PaymentType.REFUND.toString() +
                "_" +
                (isValid ? "valid" : "invalid") +
                "_" +
                vendor.getLabel() +
                "_" +
                getTime(datetime, timeFrame) +
                "_" +
                timeFrame.toString() +
                "_" +
                type.toString();
    }

    static String getKeyForRefund(Merchant merchant, Boolean isValid,
                                  OffsetDateTime datetime, AggregationTimeFrame timeFrame,
                                  AggregationType type) {
        return PaymentType.REFUND.toString() +
                "_" +
                (isValid ? "valid" : "invalid") +
                "_" +
                merchant.getLabel() +
                "_" +
                getTime(datetime, timeFrame) +
                "_" +
                timeFrame.toString() +
                "_" +
                type.toString();
    }

    static String getKey(PaymentType paymentType, OffsetDateTime datetime, AggregationTimeFrame timeFrame,
                         AggregationType aggregationType) {
        return paymentType.toString() +
                "_" +
                getTime(datetime, timeFrame) +
                "_" +
                timeFrame.toString() +
                "_" +
                aggregationType.toString();
    }

    static String getTime(OffsetDateTime datetime, AggregationTimeFrame timeFrame) {
        DateTimeFormatter df = null;
        if (timeFrame.equals(AggregationTimeFrame.MINUTE)) {
            df = minute;
        } else if (timeFrame.equals(AggregationTimeFrame.HOUR)) {
            df = hour;
        } else if (timeFrame.equals(AggregationTimeFrame.DAY)) {
            df = day;
        }

        if (df == null) {
            throw new IllegalStateException("Cannot find datetime formatter for time frame: " + timeFrame);
        }

        return df.format(datetime);
    }

    public static String getRequiredParam(String name, Map<String, List<String>> params, List<String> missingParams) {
        List<String> tmpList = params.get(name);
        if (Objects.isNull(tmpList) || tmpList.size() == 0) {
            missingParams.add(name);
            return null;
        } else {
            return tmpList.get(0);
        }
    }

    public static Optional<String> getOptionalParam(String name, Map<String, List<String>> params) {
        List<String> tmpList = params.get(name);
        if (Objects.isNull(tmpList) || tmpList.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(tmpList.get(0));
        }
    }
}
