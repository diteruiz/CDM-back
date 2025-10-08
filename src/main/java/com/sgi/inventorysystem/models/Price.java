package com.sgi.inventorysystem.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prices")
public class Price {
    @Id
    private String id;
    private String userId;   // to filter by logged user
    private String clientId; // reference to Client
    private String productName;
    private Double price;

    // Constructors
    public Price() {}

    public Price(String userId, String clientId, String productName, Double price) {
        this.userId = userId;
        this.clientId = clientId;
        this.productName = productName;
        this.price = price;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}