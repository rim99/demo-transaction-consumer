package org.example.transaction.consumer.adapter;

import org.example.transaction.consumer.port.TransactionRecordRepository;
import org.example.transaction.consumer.port.TransactionRecord;

import io.helidon.dbclient.DbClient;

public class TransactionRecordPostgresRepository implements TransactionRecordRepository {

    private DbClient pgClient;

    public TransactionRecordPostgresRepository(DbClient pgClient) {
        this.pgClient = pgClient;
    }

    @Override
    public void save(TransactionRecord record) {
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
            .execute()
        )
        .exceptionallyAccept(Throwable::printStackTrace);
    }
}
