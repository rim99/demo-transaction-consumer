package org.example.transaction.consumer.entity.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.example.transaction.consumer.port.AggregationItem;

import java.util.List;

public class AggregationItemListSerializer {

    private static class Singleton {
        static AggregationItemListSerializer instance = new AggregationItemListSerializer();
    }

    public static AggregationItemListSerializer get() {
        return Singleton.instance;
    }

    private ObjectWriter writer;

    AggregationItemListSerializer() {
        writer = new ObjectMapper().writer();
    }

    public byte[] serialize(List<AggregationItem> data) {
        if (data == null) return null;
        try {
            return writer.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
