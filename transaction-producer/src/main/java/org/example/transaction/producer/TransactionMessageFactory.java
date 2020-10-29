package org.example.transaction.producer;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class TransactionMessageFactory {

    public static Supplier<TransactionMessage> creator() {
       return () -> {
            var t = new TransactionMessage();
            t.setId(UUID.randomUUID().toString());
            t.setMid("01890432");
            t.setUid("0088723951648203");
            t.setDatetime("2020-01-01T13:26:54.281+08:00");
            t.setAmount(12.35D);
            t.setCurrency("JPY");
            t.setType("refund");
            t.setVendor("wechat-pay");
            t.setStatus("success");
            t.setIsValid(Boolean.TRUE.toString());
            t.setOriginPurchaseTransactionId(UUID.randomUUID().toString());
            return t;
        };
    }

    public static Supplier<TransactionMessage> random() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        DateTimeFormatter df = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        String[] vendor = new String[] {"ali-pay", "wechat-pay", "credit-card"};
        String[] currency = new String[] {"JPY", "USD", "CHY"};
        
        return () -> {
            int i = random.nextInt(0, 50_000);
            int mod_2 = i & 1;
            int mod_3 = i % 3;
             var t = new TransactionMessage();
             t.setId(UUID.randomUUID().toString());
             t.setMid(String.format("%08d", random.nextInt(100000000)));
             t.setUid(String.format("%016d", random.nextLong(10_000_000_000_000_000L)));
             t.setDatetime(OffsetDateTime.now().format(df));
             t.setAmount( i / 100D);
             t.setCurrency(currency[mod_3]);
             t.setType(mod_2 == 1 ? "purchase" : "refund");
             t.setVendor(vendor[mod_3]);
             t.setStatus(mod_2 == 1 ? "success" : "failed");
             t.setIsValid(mod_2 == 1 ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
             t.setOriginPurchaseTransactionId(UUID.randomUUID().toString());
             return t;
         };
     }
}
