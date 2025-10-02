package com.sgi.inventorysystem.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String name;

    // 🔹 Guardar proveedor como referencia + nombre
    private String supplierId;
    private String supplierName;

    private String brandId;
    private String categoryId;
    private String userId;

    // 🔹 Location (coldstorage1, coldstorage2, warehouse, refrigerators)
    private String location;

    private Double fixedWeight;   // null if not applicable
    private boolean hasBaseWeight; // true = uses fixedWeight, false = variable weight

    private Date createdAt;
    private Date updatedAt;

    public Product() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Product(String name, String supplierId, String supplierName,
                   String brandId, String categoryId,
                   Double fixedWeight, boolean hasBaseWeight,
                   String userId, String location) {
        this.name = name;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.fixedWeight = fixedWeight;
        this.hasBaseWeight = hasBaseWeight;
        this.userId = userId;
        this.location = location;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getBrandId() { return brandId; }
    public void setBrandId(String brandId) { this.brandId = brandId; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getFixedWeight() { return fixedWeight; }
    public void setFixedWeight(Double fixedWeight) { this.fixedWeight = fixedWeight; }

    public boolean isHasBaseWeight() { return hasBaseWeight; }
    public void setHasBaseWeight(boolean hasBaseWeight) { this.hasBaseWeight = hasBaseWeight; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}