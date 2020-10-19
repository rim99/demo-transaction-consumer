package org.example.transaction.consumer.adapter;

import org.example.transaction.consumer.port.TransactionMessageRepository;
import org.example.transaction.consumer.port.TransactionRecord;

import io.helidon.dbclient.DbClient;

public class TransactionMessagePostgresRepository implements TransactionMessageRepository {

    private DbClient pgClient;

    public TransactionMessagePostgresRepository(DbClient pgClient) {
        this.pgClient = pgClient;
    }

    @Override
    public void save(TransactionRecord record) {
        pgClient.execute(exec -> exec
            .createInsert("INSERT INTO transaction_message VALUES (" 
                + " :id , :tid , :uid , :datetime , :amount , "    
                + " CAST( :currency AS PAYMENT_CURRENCY) ," 
                + " CAST( :type AS PAYMENT_TYPE) ," 
                + " CAST( :vendor AS PAYMENT_VENDOR) ," 
                + " CAST( :status AS PAYMENT_STATUS) "  
                + ")"
            )
            .addParam("id", record.getId())     
            .addParam("tid", record.getTid())     
            .addParam("uid", record.getUid())
            .addParam("datetime", record.getDatetime())    
            .addParam("amount", record.getAmount())
            .addParam("currency", record.getCurrency().toString())
            .addParam("type", record.getType().getLabel())   
            .addParam("vendor", record.getVendor().getLabel())  
            .addParam("status", record.getStatus().getLabel())  
            .execute()
        )
        .thenAccept(c -> System.out.println("inserted " + c + " records"))
        .exceptionallyAccept(t -> t.printStackTrace());
    }
}
