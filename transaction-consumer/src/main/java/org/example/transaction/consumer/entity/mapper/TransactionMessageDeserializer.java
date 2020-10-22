package org.example.transaction.consumer.entity.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
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

    private ObjectReader reader;

    TransactionMessageDeserializer() {
        reader = new ObjectMapper().reader();
    }

    public Optional<TransactionMessage> deserialize(final byte[] data) {
        if (data != null) {
            try {
                return Optional.ofNullable(
                        reader.readValue(data, TransactionMessage.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}
