package org.example.transaction.producer;

import java.util.function.Supplier;

public class TransactionMessageFactory {

    public static Supplier<TransactionMessage> creator() {
        var t = new TransactionMessage();
        t.setId("8AE281B8-9EBE-4026-AFD8-FC980D0E8325");
        t.setTid("01890432");
        t.setUid("0088723951648203");
        t.setDatetime("2020-01-01T13:26:54.281+08:00");
        t.setAmount(12.35f);
        t.setCurrency("JPY");
        t.setType("purchase");
        t.setVendor("wechat-pay");
        t.setStatus("success");
        return () -> t;
    }
}
