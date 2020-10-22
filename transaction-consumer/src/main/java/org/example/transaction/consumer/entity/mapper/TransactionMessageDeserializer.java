package org.example.transaction.consumer.entity.mapper;

import java.io.IOException;
import java.util.Optional;

import com.dslplatform.json.DslJson;

import org.example.transaction.consumer.entity.TransactionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionMessageDeserializer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionMessageDeserializer.class);

    private final DslJson<TransactionMessage> json = new DslJson<>();

    public Optional<TransactionMessage> deserialize(final byte[] data) {
        try {
            if (data != null) {
                return Optional.ofNullable(
                    json.deserialize(TransactionMessage.class, data, data.length));
            } 
        } catch (IOException e) {
            logger.error("Error when deserialize TransactionMessage");
        }
        return Optional.empty();
    }
}
