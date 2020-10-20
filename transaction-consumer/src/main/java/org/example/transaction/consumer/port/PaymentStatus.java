package org.example.transaction.consumer.port;

public enum PaymentStatus {
    SUCCESS("success"),
    FAILED("failed");

    private String label;

    PaymentStatus(String label) {
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
