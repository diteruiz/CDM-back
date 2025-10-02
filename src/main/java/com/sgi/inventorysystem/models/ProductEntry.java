package com.sgi.inventorysystem.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "product_entries")
public class ProductEntry {

    @Id
    private String id;

    private String productId;
    private String productName;
    private String brandId;

    // 🔹 Guardar proveedor como referencia y nombre
    private String supplierId;
    private String supplierName;

    // Applies only if product has base/fixed weight
    private int quantityEntered;

    // Total weight in KG (fixed: qty * fixedWeight, variable: sum of ProductWeight)
    private Double totalWeight;

    // For variable weight products: list of weightIds used in this entry
    private List<String> weightIds;

    private Date enteredAt;
    private String userId;

    // New field for notes (reason, for who, etc.)
    private String notes;

    // 🔹 New field for location (coldstorage1, coldstorage2, warehouse, refrigerators)
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

    public int getQuantityEntered() { return quantityEntered; }
    public void setQuantityEntered(int quantityEntered) { this.quantityEntered = quantityEntered; }

    public Double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(Double totalWeight) { this.totalWeight = totalWeight; }

    public List<String> getWeightIds() { return weightIds; }
    public void setWeightIds(List<String> weightIds) { this.weightIds = weightIds; }

    public Date getEnteredAt() { return enteredAt; }
    public void setEnteredAt(Date enteredAt) { this.enteredAt = enteredAt; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}