package org.example.transaction.consumer.entity.mapper;

import com.dslplatform.json.DslJson;
import org.example.transaction.consumer.port.AggregationItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class AggregationItemListSerializer {

    private final DslJson<AggregationItem> json = new DslJson<>();

    public byte[] serialize(List<AggregationItem> data) {
        if (data == null) return null;
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        try {
            json.serialize(data, ba);
            return ba.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
