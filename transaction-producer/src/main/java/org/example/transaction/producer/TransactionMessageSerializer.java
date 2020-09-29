package org.example.transaction.producer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.dslplatform.json.DslJson;

public class TransactionMessageSerializer {

    private final DslJson<TransactionMessage> json = new DslJson<>();

    public byte[] serialize(TransactionMessage data) {
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
