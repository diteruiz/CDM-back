package com.sgi.inventorysystem.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_templates")
public class ProductTemplate {

    @Id
    private String id;

    private String name;
    private String categoryId;
    private String brandId;

    // 🔹 Referencia al modelo Supplier
    private String supplierId;
    private String supplierName; // 👈 añadido para mostrar directamente el nombre

    // 🔹 Legacy: para productos viejos que ya tienen el campo como texto
    private String supplier;

    private Double fixedWeight;    // null si no aplica
    private boolean hasBaseWeight; // true = usa fixedWeight, false = variable weight

    private String userId;

    // --- Constructors ---
    public ProductTemplate() {}

    public ProductTemplate(String name, String categoryId, String brandId,
                           String supplierId, String supplierName, String supplier,
                           Double fixedWeight, boolean hasBaseWeight, String userId) {
        this.name = name;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplier = supplier;
        this.fixedWeight = fixedWeight;
        this.hasBaseWeight = hasBaseWeight;
        this.userId = userId;
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getBrandId() { return brandId; }
    public void setBrandId(String brandId) { this.brandId = brandId; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public Double getFixedWeight() { return fixedWeight; }
    public void setFixedWeight(Double fixedWeight) { this.fixedWeight = fixedWeight; }

    public boolean isHasBaseWeight() { return hasBaseWeight; }
    public void setHasBaseWeight(boolean hasBaseWeight) { this.hasBaseWeight = hasBaseWeight; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}