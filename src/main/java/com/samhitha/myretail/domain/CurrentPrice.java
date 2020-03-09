package com.samhitha.myretail.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CurrentPrice implements Serializable {

    Double value;

    @JsonProperty("currency_code")
    String currencyCode;

    public Double getValue() {
        return value;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    CurrentPrice(Double value, String currencyCode) {
        this.value = value;
        this.currencyCode = currencyCode;
    }
}