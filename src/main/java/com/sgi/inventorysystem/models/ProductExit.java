package com.sgi.inventorysystem.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Document(collection = "product_exits")
public class ProductExit {

    @Id
    private String id;

    private String productId;
    private String productName;

    // 🔹 Brand info
    private String brandId;
    private String brandName;

    // 🔹 Category info
    private String categoryId;
    private String categoryName;

    // 🔹 Supplier info
    private String supplierId;
    private String supplierName;

    // Applies only if product has base/fixed weight
    private int quantityExited;

    // Total weight in KG (fixed: qty * fixedWeight, variable: sum of deducted weights or manual weight)
    private Double totalWeight;

    // IDs of consumed weights (for variable weight products)
    private List<String> weightIds;

    // When manual weight from scale is entered (bags, boxes without individual registration)
    private Double manualWeight;

    private Date exitedAt;
    private String userId;

    // Notes
    private String notes;

    // 🔹 Location (coldstorage1, coldstorage2, warehouse, refrigerators)
    private String location;

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBrandId() { return brandId; }
    public void setBrandId(String brandId) { this.brandId = brandId; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public int getQuantityExited() { return quantityExited; }
    public void setQuantityExited(int quantityExited) { this.quantityExited = quantityExited; }

    public Double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(Double totalWeight) { this.totalWeight = totalWeight; }

    public List<String> getWeightIds() { return weightIds; }
    public void setWeightIds(List<String> weightIds) { this.weightIds = weightIds; }

    public Double getManualWeight() { return manualWeight; }
    public void setManualWeight(Double manualWeight) { this.manualWeight = manualWeight; }

    public Date getExitedAt() { return exitedAt; }
    public void setExitedAt(Date exitedAt) { this.exitedAt = exitedAt; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}