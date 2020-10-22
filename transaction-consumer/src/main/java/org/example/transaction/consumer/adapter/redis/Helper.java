package org.example.transaction.consumer.adapter.redis;

import org.example.transaction.consumer.port.AggregationTimeFrame;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

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

}
