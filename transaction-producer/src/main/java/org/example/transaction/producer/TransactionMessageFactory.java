package org.example.transaction.producer;

import java.util.UUID;
import java.util.function.Supplier;

public class TransactionMessageFactory {

    public static Supplier<TransactionMessage> creator() {
       return () -> {
            var t = new TransactionMessage();
            t.setId(UUID.randomUUID().toString());
            t.setTid("01890432");
            t.setUid("0088723951648203");
            t.setDatetime("2020-01-01T13:26:54.281+08:00");
            t.setAmount(12.35f);
            t.setCurrency("JPY");
            t.setType("purchase");
            t.setVendor("wechat-pay");
            t.setStatus("success");
            return t;
        };
    }
}