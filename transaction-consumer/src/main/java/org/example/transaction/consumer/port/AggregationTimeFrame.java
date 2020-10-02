package org.example.transaction.consumer.port;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum AggregationTimeFrame {
    MINUTE, HOUR, DAY;

    static final List<AggregationTimeFrame> all = Collections.unmodifiableList(new ArrayList<>(3){{
        add(MINUTE);
        add(HOUR);
        add(DAY);
    }});

    public static List<AggregationTimeFrame> getAll() {
        return all;
    }
}
