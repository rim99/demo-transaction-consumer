package org.example.transaction.consumer.port;

public enum PaymentType {
    PURCHASE("purchase"),
    REFUND("refund");

    private String label;

    PaymentType(String label) {
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
