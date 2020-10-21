package org.example.transaction.consumer.port;

public class Merchant {

    private Integer id;

    public Merchant(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return "Merchant-" + id;
    }
}
