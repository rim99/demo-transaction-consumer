package org.example.transaction.consumer.port;

public enum PaymentVendor {
    ALI_PAY("ali-pay"),
    WECHAT_PAY("wechat-pay"),
    CREDIT_CARD("credit-card");

    private String label;

    PaymentVendor(String label) {
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
