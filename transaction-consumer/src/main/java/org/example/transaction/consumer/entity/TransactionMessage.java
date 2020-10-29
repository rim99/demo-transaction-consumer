package org.example.transaction.consumer.entity;

import com.dslplatform.json.CompiledJson;
import org.example.transaction.consumer.port.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@CompiledJson
public class TransactionMessage {

    private String id;
    private String mid;
    private String uid;
    private String datetime;
    private double amount;
    private String currency;
    private String type;
    private String vendor;
    private String status;
    private String isValid;
    private String originPurchaseTransactionId;

    public TransactionMessage() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String valid) {
        isValid = valid;
    }

    public String getOriginPurchaseTransactionId() {
        return originPurchaseTransactionId;
    }

    public void setOriginPurchaseTransactionId(String originPurchaseTransactionId) {
        this.originPurchaseTransactionId = originPurchaseTransactionId;
    }

    @Override
    public String toString() {
        return "TransactionMessage{" +
                "id='" + id + '\'' +
                ", mid='" + mid + '\'' +
                ", uid='" + uid + '\'' +
                ", datetime='" + datetime + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", type='" + type + '\'' +
                ", vendor='" + vendor + '\'' +
                ", status='" + status + '\'' +
                ", isValid='" + isValid + '\'' +
                ", originPurchaseTransactionId='" + originPurchaseTransactionId + '\'' +
                '}';
    }

    public TransactionRecord toTransactionRecord() {
        return new TransactionRecord(
                UUID.fromString(this.id),
                Integer.parseInt(this.mid),
                Long.parseLong(this.uid),
                OffsetDateTime.parse(this.datetime, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                this.amount,
                PaymentCurrency.valueOf(this.currency),
                PaymentType.getByLabel(this.type),
                PaymentVendor.getByLabel(this.vendor),
                PaymentStatus.getByLabel(this.status),
                Boolean.valueOf(this.isValid),
                originPurchaseTransactionId == null ? null : UUID.fromString(originPurchaseTransactionId)
        );
    }
}