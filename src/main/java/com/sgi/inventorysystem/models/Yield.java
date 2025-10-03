package com.sgi.inventorysystem.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "yields")
public class Yield {

    @Id
    private String id;

    private String productName;     // 👈 nuevo campo
    private String date;            // yyyy-MM-dd
    private double baseWeight;      // used only in fixed mode
    private int boxes;              // number of boxes (or boxWeights.size())
    private double totalBase;       // calculated base
    private double totalObtained;   // sum of barrels
    private double weightDifference;
    private double yieldRatio;

    private String mode; // "fixed" or "variable"

    private List<Double> boxWeights; // list of individual box weights (variable mode)
    private List<Double> barrels;    // list of barrels always

    private String userId; // owner user

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getBaseWeight() { return baseWeight; }
    public void setBaseWeight(double baseWeight) { this.baseWeight = baseWeight; }

    public int getBoxes() { return boxes; }
    public void setBoxes(int boxes) { this.boxes = boxes; }

    public double getTotalBase() { return totalBase; }
    public void setTotalBase(double totalBase) { this.totalBase = totalBase; }

    public double getTotalObtained() { return totalObtained; }
    public void setTotalObtained(double totalObtained) { this.totalObtained = totalObtained; }

    public double getWeightDifference() { return weightDifference; }
    public void setWeightDifference(double weightDifference) { this.weightDifference = weightDifference; }

    public double getYieldRatio() { return yieldRatio; }
    public void setYieldRatio(double yieldRatio) { this.yieldRatio = yieldRatio; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public List<Double> getBoxWeights() { return boxWeights; }
    public void setBoxWeights(List<Double> boxWeights) { this.boxWeights = boxWeights; }

    public List<Double> getBarrels() { return barrels; }
    public void setBarrels(List<Double> barrels) { this.barrels = barrels; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}