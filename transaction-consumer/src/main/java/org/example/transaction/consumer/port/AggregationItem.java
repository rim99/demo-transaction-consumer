package org.example.transaction.consumer.port;

import com.dslplatform.json.CompiledJson;

@CompiledJson
public class AggregationItem {

    String timestamp;
    Double value;

    public AggregationItem(String timestamp, Double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Double getValue() {
        return value;
    }
}
