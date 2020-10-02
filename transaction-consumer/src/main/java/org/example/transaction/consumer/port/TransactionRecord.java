package org.example.transaction.consumer.port;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public class TransactionRecord {

    private UUID id;
    private Integer mid;
    private Long uid;
    private OffsetDateTime datetime;
    private double amount;
    private PaymentCurrency currency;
    private PaymentType type;
    private PaymentVendor vendor;
    private PaymentStatus status;
    private Boolean isValid;
    private UUID originPurchaseTransactionId;

    public TransactionRecord(UUID id, Integer mid, Long uid, OffsetDateTime datetime, double amount,
                             PaymentCurrency currency, PaymentType type, PaymentVendor vendor, PaymentStatus status,
                             Boolean isValid, UUID originPurchaseTransactionId) {
        this.id = id;
        this.mid = mid;
        this.uid = uid;
        this.datetime = datetime;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.vendor = vendor;
        this.status = status;
        this.isValid = isValid;
        this.originPurchaseTransactionId = originPurchaseTransactionId;
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

    public double getAmount() {
        return amount;
    }

    public OffsetDateTime getDatetime() {
        return datetime;
    }

    public Long getUid() {
        return uid;
    }

    public Integer getMid() {
        return mid;
    }

    public UUID getId() {
        return id;
    }

    public Optional<Boolean> getValid() {
        return Optional.ofNullable(isValid);
    }

    public Optional<UUID> getOriginPurchaseTransactionId() {
        return Optional.ofNullable(originPurchaseTransactionId);
    }

    @Override
    public String toString() {
        return "TransactionRecord{" +
                "id=" + id +
                ", mid=" + mid +
                ", uid=" + uid +
                ", datetime=" + datetime +
                ", amount=" + amount +
                ", currency=" + currency +
                ", type=" + type +
                ", vendor=" + vendor +
                ", status=" + status +
                ", isValid=" + isValid +
                ", originPurchaseTransactionId=" + originPurchaseTransactionId +
                '}';
    }
}
