package org.example.transaction.consumer.entity.mapper;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import org.example.transaction.consumer.port.AggregationItem;

import java.io.IOException;
import java.util.List;

public class AggregationItemListSerializer {

    private static class Singleton {
        static AggregationItemListSerializer instance = new AggregationItemListSerializer();
    }

    public static AggregationItemListSerializer get() {
        return Singleton.instance;
    }

    private final DslJson<AggregationItem> json;
    private ThreadLocal<JsonWriter> writer;

    AggregationItemListSerializer() {
        json = new DslJson<>();
        writer = ThreadLocal.withInitial(json::newWriter);
    }

    public byte[] serialize(List<AggregationItem> data) {
        if (data == null) return null;
        JsonWriter w = writer.get();
        w.reset();
        try {
            json.serialize(w, data);
            return w.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
