package org.example.transaction.consumer.port;

public class AggregationItem implements Comparable<AggregationItem> {

    private String timestamp;
    private Double value;

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

    @Override
    public int compareTo(AggregationItem o) {
        return this.timestamp.compareTo(o.timestamp);
    }
}
