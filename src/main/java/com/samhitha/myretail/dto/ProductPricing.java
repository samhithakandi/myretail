package com.samhitha.myretail.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_pricing")
public class ProductPricing {

    @Id
    String productId;
    Double price;
    String currency;

    public ProductPricing() {}

    public ProductPricing(String productId, Double price, String currency) {
        this.productId = productId;
        this.price = price;
        this.currency = currency;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[productId=%s, price=%f, currency=%s]",
                productId, price, currency);
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
