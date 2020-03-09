package com.samhitha.myretail.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {

    private final Long id;

    private final String name;

    @JsonProperty("current_price")
    private CurrentPrice currentPrice;

    public Product(Long id, String name, Double value, String currencyCode) {
        this.id = id;
        this.name = name;
        this.currentPrice = new CurrentPrice(value, currencyCode);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CurrentPrice getCurrentPrice() {
        return currentPrice;
    }

}

