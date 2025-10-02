package com.sgi.inventorysystem.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "product_weights")
public class ProductWeight {

    @Id
    private String id;

    private String productId; // Reference to Product
    private Double weight;    // Real weight of this box/bag
    private String userId;    // To filter by user
    private String location;  // 👈 New: to separate ColdStorage1, ColdStorage2, etc.

    private boolean consumed; // true if already used in an exit
    private Date createdAt;   // registration date

    public ProductWeight() {
        this.consumed = false;
        this.createdAt = new Date();
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isConsumed() { return consumed; }
    public void setConsumed(boolean consumed) { this.consumed = consumed; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}