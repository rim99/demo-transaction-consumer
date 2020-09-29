package org.example.transaction.producer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitmqMessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitmqMessageProducer.class);

    private Connection connection;
    private String queueName;

    private RabbitmqMessageProducer(Connection connection, String queueName) throws IOException {
        this.connection = connection;
        this.queueName = queueName;
    }

    public void produce(MessageGenerator gen) throws IOException, TimeoutException {
        try (Channel channel = connection.createChannel()) {
            channel.queueDeclare(this.queueName, false, false, false, null);
            gen.generate().forEach(msg -> 
                {
                    try {
                        channel.basicPublish("", this.queueName, null, msg);
                    } catch (IOException e) {
                        logger.error("Failed send message", e);
                    }
                }
            );
        }
    }

    public static class Builder {

        private Builder() {}

        public static RabbitmqMessageProducer build(String host, String username, String password, String queueName) throws IOException, TimeoutException {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setUsername(username);
            factory.setPassword(password);

            System.out.println("Connecting to RabbitMQ: host: " + host + ", username: " + username + ", password: " + password);

            Connection connection = factory.newConnection();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { 
                    connection.close();
                    System.out.println("RabbitMQ Connection closed");
                } catch (Exception e) {
                    System.out.println("failed to close RabbitMQ Connection, detail:" + e);
                }
            }));
            return new RabbitmqMessageProducer(connection, queueName);
        }
    }
}