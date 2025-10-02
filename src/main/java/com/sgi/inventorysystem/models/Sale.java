// src/main/java/com/sgi/inventorysystem/models/Sale.java
package com.sgi.inventorysystem.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "sales")
public class Sale {
    @Id
    private String id;

    private String userId;
    private String productName;
    private double sentWeight;
    private double returnWeight = 0;
    private LocalDate date = LocalDate.now(); // 👈 solo fecha

    // Getters & Setters
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

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getSentWeight() {
        return sentWeight;
    }
    public void setSentWeight(double sentWeight) {
        this.sentWeight = sentWeight;
    }

    public double getReturnWeight() {
        return returnWeight;
    }
    public void setReturnWeight(double returnWeight) {
        this.returnWeight = returnWeight;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Helpers
    public double getSoldWeight() {
        return sentWeight - returnWeight;
    }
}