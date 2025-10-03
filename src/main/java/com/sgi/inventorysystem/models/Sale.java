package com.sgi.inventorysystem.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
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

    // 👇 Initialize with empty lists to avoid null issues
    private List<Double> barrels = new ArrayList<>();

    private List<Double> returnBarrels = new ArrayList<>();

    // --- Getters & Setters ---
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
        this.barrels = (barrels != null) ? barrels : new ArrayList<>();
    }

    public List<Double> getReturnBarrels() {
        return returnBarrels;
    }
    public void setReturnBarrels(List<Double> returnBarrels) {
        this.returnBarrels = (returnBarrels != null) ? returnBarrels : new ArrayList<>();
    }

    // --- Helpers ---
    public double getSoldWeight() {
        double sold = sentWeight - returnWeight;
        return sold >= 0 ? sold : 0; // never negative
    }
}