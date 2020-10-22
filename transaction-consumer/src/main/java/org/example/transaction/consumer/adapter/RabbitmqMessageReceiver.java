package org.example.transaction.consumer.adapter;

import com.rabbitmq.client.*;
import org.example.transaction.consumer.entity.mapper.TransactionMessageDeserializer;
import org.example.transaction.consumer.port.Producer;
import org.example.transaction.consumer.port.TransactionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class RabbitmqMessageReceiver implements Producer<TransactionRecord> {

    private static final Logger logger = LoggerFactory.getLogger(RabbitmqMessageReceiver.class);

    private List<Consumer<TransactionRecord>> consumers;
    private Connection connection;
    private String queueName;

    private RabbitmqMessageReceiver(Connection connection, String queueName) throws IOException {
        this.consumers = new LinkedList<>();
        this.connection = connection;
        this.queueName = queueName;
    }

    public void start() throws IOException {
        connection.openChannel()
            .orElseThrow(() -> new IllegalStateException("Can not reach to RabbitMQ server"))
            .queueDeclareNoWait(this.queueName, false, false, false, null);
        recv();
    }

    @Override
    synchronized public void subscribe(Consumer<TransactionRecord> c) {
        consumers.add(c);
    }

    private void recv() throws IOException {
        Channel channel = connection.createChannel();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            TransactionMessageDeserializer.get().deserialize(delivery.getBody()).ifPresent(msg -> {
                TransactionRecord record = msg.toTransactionRecord();
                this.consumers.forEach(c -> c.accept(record));
            });
        };
        CancelCallback cancelCallback = consumerTag -> {
            logger.error(" [x] Consumer " + consumerTag + " is cancelled'");
        };
        channel.basicConsume(this.queueName, true, deliverCallback, cancelCallback);
    }

    public static class Builder {

        private Builder() {}

        public static RabbitmqMessageReceiver build(String host, String username, String password, String queueName) throws IOException, TimeoutException {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setUsername(username);
            factory.setPassword(password);
            Connection connection = factory.newConnection();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { 
                    connection.close();
                    System.out.println("RabbitMq Connection closed");
                } catch (Exception e) {
                    System.out.println("failed to close RabbitMq Connection, detail:" + e);
                }
            }));
            return new RabbitmqMessageReceiver(connection, queueName);
        }
    }
}