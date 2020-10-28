package org.example.transaction.consumer.adapter;

import io.helidon.dbclient.DbClient;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import org.example.transaction.consumer.port.TransactionRecord;
import org.example.transaction.consumer.port.TransactionRecordRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransactionRecordPostgresRepository implements TransactionRecordRepository {

    private DbClient pgClient;

    @Inject
    public TransactionRecordPostgresRepository(DbClient pgClient) {
        this.pgClient = pgClient;
    }

    @Override
    public void save(TransactionRecord record) {
        Histogram.Timer t = timer.startTimer();
        pgClient.execute(exec -> exec
            .createInsert("INSERT INTO transaction_message VALUES (" 
                + " :id , :mid , :uid , :datetime , :amount , "    
                + " CAST( :currency AS PAYMENT_CURRENCY) ," 
                + " CAST( :type AS PAYMENT_TYPE) ," 
                + " CAST( :vendor AS PAYMENT_VENDOR) ," 
                + " CAST( :status AS PAYMENT_STATUS) ,"  
                + " :isValid , :originPurchaseTransactionId "
                + ")"
            )
            .addParam("id", record.getId())     
            .addParam("mid", record.getMid())
            .addParam("uid", record.getUid())
            .addParam("datetime", record.getDatetime())    
            .addParam("amount", record.getAmount())
            .addParam("currency", record.getCurrency().toString())
            .addParam("type", record.getType().getLabel())   
            .addParam("vendor", record.getVendor().getLabel())  
            .addParam("status", record.getStatus().getLabel())  
            .addParam("isValid", record.getValid().orElse(null))
            .addParam("originPurchaseTransactionId", record.getOriginPurchaseTransactionId().orElse(null))
            .execute())
                .thenAccept(savedTotal -> t.observeDuration())
                .exceptionallyAccept(Throwable::printStackTrace);
    }

    private static final Histogram timer;
    static {
        timer = Histogram
                .build("postgres_persist_response_time", "response time of Postgres")
                .create();
        CollectorRegistry.defaultRegistry.register(timer);
    }
}
