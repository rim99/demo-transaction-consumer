package org.example.transaction.consumer.port;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TransactionRecord {

    private UUID id;
    private Integer tid;
    private Long uid;
    private OffsetDateTime datetime;
    private Float amount;
    private PaymentCurrency currency;
    private PaymentType type;
    private PaymentVendor vendor;
    private PaymentStatus status;

    public TransactionRecord(UUID id, Integer tid, Long uid, OffsetDateTime datetime, Float amount,
            PaymentCurrency currency, PaymentType type, PaymentVendor vendor, PaymentStatus status) {
        this.id = id;
        this.tid = tid;
        this.uid = uid;
        this.datetime = datetime;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.vendor = vendor;
        this.status = status;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentVendor getVendor() {
        return vendor;
    }

    public PaymentType getType() {
        return type;
    }

    public PaymentCurrency getCurrency() {
        return currency;
    }

    public float getAmount() {
        return amount;
    }

    public OffsetDateTime getDatetime() {
        return datetime;
    }

    public Long getUid() {
        return uid;
    }

    public Integer getTid() {
        return tid;
    }

    public UUID getId() {
        return id;
    }

    public static enum PaymentCurrency {
        CHY, USD, JPY
    }

    public static enum PaymentVendor {
        ALI_PAY("ali-pay"), 
        WECHAT_PAY("wechat-pay"), 
        CREDIT_CARD("credit-card");

        private String label;

        private PaymentVendor(String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }

        public static PaymentVendor getByLabel(String label) {
            if ("ali-pay".equals(label)) {
                return ALI_PAY;
            } else if ("wechat-pay".equals(label)) {
                return WECHAT_PAY;
            } else if ("credit-card".equals(label)) {
                return CREDIT_CARD;
            } else {
                throw new IllegalArgumentException("Cannot find payment vendor for " + label);
            }
        }
    }

    public static enum PaymentType {
        PURCHASE("purchase"), 
        REFUND("refund");

        private String label;

        private PaymentType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }

        public static PaymentType getByLabel(String label) {
            if ("purchase".equals(label)) {
                return PURCHASE;
            } else if ("refund".equals(label)) {
                return REFUND;
            } else {
                throw new IllegalArgumentException("Cannot find payment type for " + label);
            }
        }
    }

    public static enum PaymentStatus {
        SUCCESS("success"), 
        FAILED("failed");

        private String label;

        private PaymentStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }

        public static PaymentStatus getByLabel(String label) {
            if ("success".equals(label)) {
                return SUCCESS;
            } else if ("failed".equals(label)) {
                return FAILED;
            } else {
                throw new IllegalArgumentException("Cannot find payment status for " + label);
            }
        }
    }
}
