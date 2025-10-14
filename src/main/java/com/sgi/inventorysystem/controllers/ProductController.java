package com.sgi.inventorysystem.controllers;

import com.sgi.inventorysystem.models.Product;
import com.sgi.inventorysystem.models.ProductEntry;
import com.sgi.inventorysystem.models.ProductExit;
import com.sgi.inventorysystem.models.ProductWeight;
import com.sgi.inventorysystem.models.Supplier;
import com.sgi.inventorysystem.models.Brand;
import com.sgi.inventorysystem.repositories.SupplierRepository;
import com.sgi.inventorysystem.repositories.ProductWeightRepository;
import com.sgi.inventorysystem.repositories.CategoryRepository;
import com.sgi.inventorysystem.services.ExcelExportService;
import com.sgi.inventorysystem.services.ExcelImportService;
import com.sgi.inventorysystem.services.ProductService;
import com.sgi.inventorysystem.services.ProductService.ProductSummary;
import com.sgi.inventorysystem.services.ProductService.Totals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private ProductWeightRepository productWeightRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // 🔹 Helper: normalize strings
    private static String normalize(String s) {
        return (s == null) ? null : s.trim().toLowerCase();
    }

    // 🔹 Helper: parse date (yyyy-MM-dd)
    private static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            LocalDate ld = LocalDate.parse(dateStr);
            return java.sql.Timestamp.valueOf(ld.atStartOfDay());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // --- Totals for Dashboard ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/totals")
    public ResponseEntity<Totals> getTotals(@RequestParam(required = false) String location) {
        String loc = normalize(location);
        return ResponseEntity.ok(productService.getTotals(getUserId(), loc));
    }

    // --- GET all products with supplierName and categoryName resolved ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllProducts(
            @RequestParam(required = false) String location) {

        String loc = normalize(location);
        List<Product> products = productService.getAllProducts(getUserId(), loc);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Product p : products) {
            Map<String, Object> item = buildProductResponse(p);
            response.add(item);
        }

        return ResponseEntity.ok(response);
    }

    // --- GET product by ID with supplierName and categoryName ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable String id) {
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(buildProductResponse(productOpt.get()));
    }

    // --- Available Weights ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{productId}/weights")
    public ResponseEntity<List<ProductWeight>> getAvailableWeights(
            @PathVariable String productId,
            @RequestParam(required = false) String location) {

        String loc = normalize(location);
        List<ProductWeight> available;
        if (loc != null && !loc.isBlank()) {
            available = productWeightRepository.findByProductIdAndUserIdAndLocationAndConsumedFalse(
                    productId, getUserId(), loc
            );
        } else {
            available = productWeightRepository.findByProductIdAndUserIdAndConsumedFalse(
                    productId, getUserId()
            );
        }

        return ResponseEntity.ok(available);
    }

    // --- Products by Category ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    // --- Products by Brand ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<Product>> getProductsByBrand(@PathVariable String brandId) {
        return ResponseEntity.ok(productService.getProductsByBrand(brandId));
    }

    // --- Create Product ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        product.setUserId(getUserId());
        if (product.getLocation() == null || product.getLocation().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        product.setLocation(normalize(product.getLocation()));
        return ResponseEntity.ok(productService.createProduct(product));
    }

    // --- Update Product ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        if (product.getLocation() != null) {
            product.setLocation(normalize(product.getLocation()));
        }
        Optional<Product> updated = productService.updateProduct(id, product);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- Delete Product ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        boolean deleted = productService.deleteProduct(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // --- Excel Export ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportProductsToExcel() {
        try {
            ByteArrayInputStream stream = excelExportService.exportProductsToExcel(getUserId());
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=products.xlsx")
                    .body(stream.readAllBytes());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- Excel Import ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/import")
    public ResponseEntity<List<Product>> importProductsFromExcel(@RequestParam("file") MultipartFile file) {
        List<Product> importedProducts = excelImportService.importProductsFromExcel(file, getUserId());
        if (importedProducts.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(importedProducts);
    }

    // --- Entries ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/entry")
    public ResponseEntity<ProductEntry> registerEntry(
            @RequestBody Map<String, Object> body,
            @RequestParam(required = false) String location) {

        String userId = getUserId();

        String templateId = (String) body.get("templateId");
        Integer quantity = (body.get("quantity") != null) ? ((Number) body.get("quantity")).intValue() : 0;
        List<String> weightIds = (List<String>) body.getOrDefault("weightIds", new ArrayList<>());

        @SuppressWarnings("unchecked")
        List<Number> weights = (List<Number>) body.getOrDefault("weights", new ArrayList<>());

        Double averageWeight = body.get("averageWeight") != null ? ((Number) body.get("averageWeight")).doubleValue() : null;
        Double totalWeight = body.get("totalWeight") != null ? ((Number) body.get("totalWeight")).doubleValue() : null;
        String notes = (String) body.get("notes");
        String supplierId = (String) body.get("supplierId");
        Date customDate = parseDate((String) body.get("date"));

        ProductEntry entry = productService.registerEntry(
                templateId,
                quantity,
                weightIds,
                weights,
                averageWeight,
                totalWeight,
                userId,
                notes,
                location,
                supplierId,
                customDate
        );

        if (entry == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(entry);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/entries")
    public ResponseEntity<List<ProductEntry>> getEntries(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String date) {
        String loc = normalize(location);
        List<ProductEntry> entries;
        if (date != null && !date.isBlank()) {
            entries = productService.getEntriesByDate(getUserId(), loc, date);
        } else {
            entries = productService.getEntries(getUserId(), loc);
        }
        for (ProductEntry e : entries) {
            if (e.getSupplierId() != null && (e.getSupplierName() == null || e.getSupplierName().isBlank())) {
                supplierRepository.findById(e.getSupplierId())
                        .ifPresent(s -> e.setSupplierName(s.getName()));
            }
        }
        return ResponseEntity.ok(entries);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/entries/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable String id) {
        boolean deleted = productService.deleteEntry(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // --- ✅ NEW: Export weights of a specific entry ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/entries/{entryId}/export")
    public ResponseEntity<byte[]> exportEntryWeights(@PathVariable String entryId) {
        try {
            Optional<ProductEntry> entryOpt = productService.getEntryById(entryId);
            if (entryOpt.isEmpty()) return ResponseEntity.notFound().build();

            ProductEntry entry = entryOpt.get();

            List<ProductWeight> weights = new ArrayList<>();
            if (entry.getWeights() != null && !entry.getWeights().isEmpty()) {
                weights = productWeightRepository.findAllById(entry.getWeights());
            }

            Brand brand = null;
            Supplier supplier = null;

            if (entry.getBrandId() != null) {
                brand = productService.getBrandById(entry.getBrandId()).orElse(null);
            }
            if (entry.getSupplierId() != null) {
                supplier = supplierRepository.findById(entry.getSupplierId()).orElse(null);
            }

            ByteArrayInputStream stream = excelExportService.exportEntryWeightsToExcel(entry, weights, brand, supplier);

            String filename = "weights_" + entry.getProductName().replaceAll("\\s+", "_") + ".xlsx";

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + filename)
                    .body(stream.readAllBytes());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- Exits ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/exit/{productId}")
    public ResponseEntity<ProductExit> registerExit(
            @PathVariable String productId,
            @RequestBody Map<String, Object> body,
            @RequestParam(required = false) String location) {

        String userId = getUserId();

        Integer quantity = (body.get("quantity") != null) ? ((Number) body.get("quantity")).intValue() : 0;
        Double manualWeight = body.get("manualWeight") != null ? ((Number) body.get("manualWeight")).doubleValue() : null;
        List<String> weightIds = (List<String>) body.getOrDefault("weightIds", new ArrayList<>());
        String notes = (String) body.get("notes");
        Date customDate = parseDate((String) body.get("date"));

        ProductExit exit = productService.registerExit(
                productId,
                manualWeight,
                quantity,
                weightIds,
                userId,
                notes,
                location,
                customDate
        );

        if (exit == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(exit);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/exits")
    public ResponseEntity<List<ProductExit>> getExits(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String date) {
        String loc = normalize(location);
        List<ProductExit> exits;
        if (date != null && !date.isBlank()) {
            exits = productService.getExitsByDate(getUserId(), loc, date);
        } else {
            exits = productService.getExits(getUserId(), loc);
        }
        for (ProductExit e : exits) {
            if (e.getSupplierId() != null && (e.getSupplierName() == null || e.getSupplierName().isBlank())) {
                supplierRepository.findById(e.getSupplierId())
                        .ifPresent(s -> e.setSupplierName(s.getName()));
            }
        }
        return ResponseEntity.ok(exits);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/exits/{id}")
    public ResponseEntity<Void> deleteExit(@PathVariable String id) {
        boolean deleted = productService.deleteExit(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // --- Summary ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/summary")
    public ResponseEntity<List<ProductSummary>> getProductSummary(
            @RequestParam(required = false) String location) {
        String loc = normalize(location);
        List<ProductSummary> summaries = productService.getProductSummary(getUserId(), loc);
        for (ProductSummary s : summaries) {
            if (s.getSupplierId() != null && (s.getSupplierName() == null || s.getSupplierName().isBlank())) {
                supplierRepository.findById(s.getSupplierId())
                        .ifPresent(sup -> s.setSupplierName(sup.getName()));
            }
            if (s.getCategoryId() != null && (s.getCategoryName() == null || s.getCategoryName().isBlank())) {
                categoryRepository.findById(s.getCategoryId())
                        .ifPresent(cat -> s.setCategoryName(cat.getName()));
            }
        }
        return ResponseEntity.ok(summaries);
    }

    // --- Optimized Inventory Summary ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/inventory/summary")
    public ResponseEntity<Map<String, ProductService.LocationSummary>> getInventorySummary() {
        String userId = getUserId();
        List<String> locations = List.of("coldstorage1", "coldstorage2", "refrigerators", "totems", "warehouse");
        Map<String, ProductService.LocationSummary> summary =
                productService.getInventorySummary(userId, locations);
        for (ProductService.LocationSummary locSummary : summary.values()) {
            for (ProductSummary s : locSummary.getProducts()) {
                if (s.getSupplierId() != null && (s.getSupplierName() == null || s.getSupplierName().isBlank())) {
                    supplierRepository.findById(s.getSupplierId())
                            .ifPresent(sup -> s.setSupplierName(sup.getName()));
                }
                if (s.getCategoryId() != null && (s.getCategoryName() == null || s.getCategoryName().isBlank())) {
                    categoryRepository.findById(s.getCategoryId())
                            .ifPresent(cat -> s.setCategoryName(cat.getName()));
                }
            }
        }
        return ResponseEntity.ok(summary);
    }

    // --- Clear Inventory ---
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/inventory/{productId}")
    public ResponseEntity<Void> clearInventory(
            @PathVariable String productId,
            @RequestParam(required = false) String location) {
        String loc = normalize(location);
        boolean deleted = productService.clearInventory(productId, getUserId(), loc);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // --- Helper ---
    private Map<String, Object> buildProductResponse(Product p) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", p.getId());
        item.put("name", p.getName());
        item.put("brandId", p.getBrandId());
        item.put("categoryId", p.getCategoryId());
        item.put("location", p.getLocation());
        item.put("fixedWeight", p.getFixedWeight());
        item.put("hasBaseWeight", p.isHasBaseWeight());
        item.put("createdAt", p.getCreatedAt());
        item.put("updatedAt", p.getUpdatedAt());

        String supplierName = "N/A";
        if (p.getSupplierId() != null) {
            supplierName = supplierRepository.findById(p.getSupplierId())
                    .map(Supplier::getName)
                    .orElse(p.getSupplierName() != null ? p.getSupplierName() : "N/A");
        } else if (p.getSupplierName() != null) {
            supplierName = p.getSupplierName();
        }
        item.put("supplierId", p.getSupplierId());
        item.put("supplierName", supplierName);

        String categoryName = "N/A";
        if (p.getCategoryId() != null) {
            categoryName = categoryRepository.findById(p.getCategoryId())
                    .map(c -> c.getName())
                    .orElse("N/A");
        }
        item.put("categoryName", categoryName);

        return item;
    }
}