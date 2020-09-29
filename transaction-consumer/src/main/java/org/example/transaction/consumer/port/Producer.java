package org.example.transaction.consumer.port;

import java.util.function.Consumer;

public interface Producer<T> {
    void subscribe(Consumer<T> consumer);
}
