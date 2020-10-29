package org.example.transaction.producer;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public final class Main {

    private Main() {
    }
    
    public static void main(final String[] args) throws IOException, TimeoutException {
        Properties prop = new Properties();
        prop.load(Main.class.getClassLoader().getResourceAsStream("app.properties"));
        startMessageProducer(prop);
    }

    static void startMessageProducer(Properties prop) throws IOException, TimeoutException {
        RabbitmqMessageProducer.Builder.build(
            prop.getProperty("host"),
            prop.getProperty("username"),
            prop.getProperty("password"),
            prop.getProperty("queueName")
        ).produce(new TransactionMessageGenerator(
            TransactionMessageFactory.random(), 
            Integer.parseInt(prop.getProperty("messageTotal")),
            Integer.parseInt(prop.getProperty("ratePerSecond"))
        ));
        System.exit(0);
    }
}
