package org.example.transaction.producer;

import java.util.stream.Stream;

public interface MessageGenerator {
    Stream<byte[]> generate();
}
