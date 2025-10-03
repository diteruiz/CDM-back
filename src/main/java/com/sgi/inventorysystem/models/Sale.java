package com.sgi.inventorysystem.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "sales")
public class Sale {
    @Id
    private String id;

    private String userId;
    private String productName;
    private double sentWeight;
    private double returnWeight = 0;
    private LocalDate date = LocalDate.now();

    // 👇 New: list of barrels sent (each barrel weight in KG)
    private List<Double> barrels;

    // 👇 New: list of barrels returned (each barrel weight in KG)
    private List<Double> returnBarrels;

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

    public List<Double> getBarrels() {
        return barrels;
    }
    public void setBarrels(List<Double> barrels) {
        this.barrels = barrels;
    }

    public List<Double> getReturnBarrels() {
        return returnBarrels;
    }
    public void setReturnBarrels(List<Double> returnBarrels) {
        this.returnBarrels = returnBarrels;
    }

    // Helpers
    public double getSoldWeight() {
        return sentWeight - returnWeight;
    }
}