package org.example.transaction.producer;

import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TransactionMessageGenerator implements MessageGenerator {

    private Supplier<TransactionMessage> supplier;
    private int total;
    private TransactionMessageSerializer serializer;

    public TransactionMessageGenerator(Supplier<TransactionMessage> supplier, int total, int rate) {
        this.supplier = supplier;
        this.total = total;
        this.serializer = new TransactionMessageSerializer();
    }

	@Override
	public Stream<byte[]> generate() {
		return IntStream.range(0, total)
            .mapToObj($ -> this.supplier.get())
            .map(msg -> serializer.serialize(msg))
            .filter($ -> $ != null);
	}
}
