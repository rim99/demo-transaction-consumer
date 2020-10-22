package org.example.transaction.consumer.entity.mapper;

import com.dslplatform.json.DslJson;
import org.example.transaction.consumer.entity.TransactionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class TransactionMessageDeserializer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionMessageDeserializer.class);

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
        try {
            if (data != null) {
                return Optional.ofNullable(
                    json.deserialize(TransactionMessage.class, data, data.length));
            } 
        } catch (IOException e) {
            logger.error("Error when deserialize TransactionMessage, detail:" + e);
        }
        return Optional.empty();
    }
}
