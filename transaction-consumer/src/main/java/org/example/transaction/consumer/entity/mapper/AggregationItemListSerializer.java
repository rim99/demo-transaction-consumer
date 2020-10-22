package org.example.transaction.consumer.entity.mapper;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import org.example.transaction.consumer.port.AggregationItem;

import java.io.IOException;
import java.util.List;

public class AggregationItemListSerializer {

    private final DslJson<AggregationItem> json = new DslJson<>();
    private ThreadLocal<JsonWriter> writer = ThreadLocal.withInitial(json::newWriter);

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
