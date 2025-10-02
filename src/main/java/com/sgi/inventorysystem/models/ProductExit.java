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
    private String brandId;

    // 🔹 Guardar proveedor como referencia y nombre
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