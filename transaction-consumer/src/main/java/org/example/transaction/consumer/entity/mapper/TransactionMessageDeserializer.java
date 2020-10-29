package org.example.transaction.consumer.entity.mapper;

import com.dslplatform.json.DslJson;
import org.example.transaction.consumer.entity.TransactionMessage;

import java.io.IOException;
import java.util.Optional;

public class TransactionMessageDeserializer {

    private static class Singleton {
        static TransactionMessageDeserializer instance = new TransactionMessageDeserializer();
    }

    public static TransactionMessageDeserializer get() {
        return Singleton.instance;
    }

    private final DslJson<TransactionMessage> json;

    TransactionMessageDeserializer() {
        json = new DslJson<>();
    }

    public Optional<TransactionMessage> deserialize(final byte[] data) {
        if (data != null) {
            try {
                return Optional.ofNullable(
                        json.deserialize(TransactionMessage.class, data, data.length));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}
