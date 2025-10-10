package com.sgi.inventorysystem.services;

import com.sgi.inventorysystem.models.*;
import com.sgi.inventorysystem.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductEntryRepository productEntryRepository;

    @Autowired
    private ProductExitRepository productExitRepository;

    @Autowired
    private ProductWeightRepository productWeightRepository;

    @Autowired
    private ProductTemplateRepository productTemplateRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    // 🔹 Helper to normalize location
    private static String norm(String s) {
        return (s == null) ? null : s.trim().toLowerCase();
    }

    // --- Totals for Dashboard ---
    public Totals getTotals(String userId, String location) {
        String loc = norm(location);
        long totalBoxes = 0;
        double totalWeight = 0;

        List<Product> products = productRepository.findByUserId(userId);

        for (Product p : products) {
            List<ProductWeight> remaining = (loc != null && !loc.isBlank())
                    ? productWeightRepository.findByProductIdAndUserIdAndLocationAndConsumedFalse(p.getId(), userId, loc)
                    : productWeightRepository.findByProductIdAndUserIdAndConsumedFalse(p.getId(), userId);

            long boxes = remaining.size();
            double weight = remaining.stream().mapToDouble(ProductWeight::getWeight).sum();

            // Fixed-weight fallback if there are no loose weights left
            if (p.isHasBaseWeight() && boxes == 0) {
                long entries = productEntryRepository.findByUserIdAndLocation(userId, loc)
                        .stream()
                        .filter(e -> e.getProductId().equals(p.getId()))
                        .mapToLong(ProductEntry::getQuantityEntered)
                        .sum();

                long exits = productExitRepository.findByUserIdAndLocation(userId, loc)
                        .stream()
                        .filter(x -> x.getProductId().equals(p.getId()))
                        .mapToLong(ProductExit::getQuantityExited)
                        .sum();

                long balance = entries - exits;
                if (balance > 0 && p.getFixedWeight() != null) {
                    boxes = balance;
                    weight = balance * p.getFixedWeight();
                }
            }

            totalBoxes += boxes;
            totalWeight += weight;
        }

        return new Totals(totalBoxes, totalWeight);
    }

    public static class Totals {
        private long totalBoxes;
        private double totalWeight;

        public Totals(long totalBoxes, double totalWeight) {
            this.totalBoxes = totalBoxes;
            this.totalWeight = totalWeight;
        }

        public long getTotalBoxes() { return totalBoxes; }
        public double getTotalWeight() { return totalWeight; }
    }

    // --- Products ---
    public List<Product> getAllProducts(String userId, String location) {
        String loc = norm(location);
        if (loc != null && !loc.isBlank()) {
            return productRepository.findByUserIdAndLocation(userId, loc);
        }
        return productRepository.findByUserId(userId);
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategory(String categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getProductsByBrand(String brandId) {
        return productRepository.findByBrandId(brandId);
    }

    public Product createProduct(Product product) {
        if (product.getSupplierId() != null && (product.getSupplierName() == null || product.getSupplierName().isBlank())) {
            supplierRepository.findById(product.getSupplierId())
                    .ifPresent(s -> product.setSupplierName(s.getName()));
        }
        product.setLocation(norm(product.getLocation())); // ✅ normalize
        product.setCreatedAt(new Date());
        product.setUpdatedAt(new Date());
        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(String id, Product updatedProduct) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(updatedProduct.getName());
            existing.setSupplierId(updatedProduct.getSupplierId());

            if (updatedProduct.getSupplierId() != null && (updatedProduct.getSupplierName() == null || updatedProduct.getSupplierName().isBlank())) {
                supplierRepository.findById(updatedProduct.getSupplierId())
                        .ifPresent(s -> existing.setSupplierName(s.getName()));
            } else {
                existing.setSupplierName(updatedProduct.getSupplierName());
            }

            existing.setBrandId(updatedProduct.getBrandId());
            existing.setCategoryId(updatedProduct.getCategoryId());
            existing.setFixedWeight(updatedProduct.getFixedWeight());
            existing.setHasBaseWeight(updatedProduct.isHasBaseWeight());

            if (updatedProduct.getLocation() != null) {
                existing.setLocation(norm(updatedProduct.getLocation())); // ✅ normalize
            }

            existing.setUpdatedAt(new Date());
            return productRepository.save(existing);
        });
    }

    public boolean deleteProduct(String id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) return false;

        String productId = id;

        // 1. Delete all weights linked to this product
        List<ProductWeight> weights = productWeightRepository.findByProductId(productId);
        if (!weights.isEmpty()) {
            productWeightRepository.deleteAll(weights);
        }

        // 2. Delete all entries linked to this product
        List<ProductEntry> entries = productEntryRepository.findByProductId(productId);
        if (!entries.isEmpty()) {
            productEntryRepository.deleteAll(entries);
        }

        // 3. Delete all exits linked to this product
        List<ProductExit> exits = productExitRepository.findByProductId(productId);
        if (!exits.isEmpty()) {
            productExitRepository.deleteAll(exits);
        }

        // 4. Finally delete the product
        productRepository.deleteById(productId);

        return true;
    }

    // --- Create or recover product from template ---
    public Product getOrCreateFromTemplate(String templateId, String userId, String location) {
        String loc = norm(location);
        Optional<ProductTemplate> templateOpt = productTemplateRepository.findById(templateId);
        if (templateOpt.isEmpty()) return null;

        ProductTemplate template = templateOpt.get();

        Optional<Product> existing = productRepository.findByUserIdAndLocation(userId, loc)
                .stream()
                .filter(p -> p.getName().equalsIgnoreCase(template.getName())
                        && Objects.equals(p.getBrandId(), template.getBrandId())
                        && Objects.equals(p.getCategoryId(), template.getCategoryId())
                        && Objects.equals(p.getSupplierId(), template.getSupplierId()))
                .findFirst();

        if (existing.isPresent()) return existing.get();

        Product newProduct = new Product();
        newProduct.setName(template.getName());
        newProduct.setBrandId(template.getBrandId());
        newProduct.setCategoryId(template.getCategoryId());
        newProduct.setSupplierId(template.getSupplierId());

        if (template.getSupplierId() != null && (template.getSupplierName() == null || template.getSupplierName().isBlank())) {
            supplierRepository.findById(template.getSupplierId())
                    .ifPresent(s -> newProduct.setSupplierName(s.getName()));
        } else {
            newProduct.setSupplierName(template.getSupplierName());
        }

        newProduct.setFixedWeight(template.getFixedWeight());
        newProduct.setHasBaseWeight(template.isHasBaseWeight());
        newProduct.setUserId(userId);
        newProduct.setLocation(loc); // ✅ normalize
        newProduct.setCreatedAt(new Date());
        newProduct.setUpdatedAt(new Date());

        return productRepository.save(newProduct);
    }

    // --- Entries ---
    public ProductEntry registerEntry(
            String templateId,
            int quantity,
            List<String> weightIds,
            List<Number> weights,      // ✅ accept Integer or Double to avoid Jackson cast issues
            Double averageWeight,
            Double totalWeight,
            String userId,
            String notes,
            String location,
            String supplierId,
            Date customDate
    ) {
        String loc = norm(location);
        if (templateId == null) return null;

        Product product = getOrCreateFromTemplate(templateId, userId, loc);
        if (product == null) return null;

        ProductEntry entry = new ProductEntry();
        entry.setProductId(product.getId());
        entry.setProductName(product.getName());
        entry.setBrandId(product.getBrandId());
        entry.setUserId(userId);

        // 👇 Use provided date or current
        entry.setEnteredAt(customDate != null ? customDate : new Date());

        entry.setNotes(notes);
        entry.setLocation(loc);

        // Supplier
        if (supplierId != null && !supplierId.isBlank()) {
            entry.setSupplierId(supplierId);
            supplierRepository.findById(supplierId)
                    .ifPresent(s -> entry.setSupplierName(s.getName()));
        } else if (product.getSupplierId() != null) {
            entry.setSupplierId(product.getSupplierId());
            if (product.getSupplierName() == null || product.getSupplierName().isBlank()) {
                supplierRepository.findById(product.getSupplierId())
                        .ifPresent(s -> entry.setSupplierName(s.getName()));
            } else {
                entry.setSupplierName(product.getSupplierName());
            }
        }

        // Mode average weight
        if (quantity > 0 && averageWeight != null && totalWeight != null) {
            List<ProductWeight> createdWeights = new ArrayList<>();
            for (int i = 0; i < quantity; i++) {
                ProductWeight pw = new ProductWeight();
                pw.setProductId(product.getId());
                pw.setUserId(userId);
                pw.setLocation(loc);
                pw.setWeight(averageWeight);
                pw.setConsumed(false);
                createdWeights.add(productWeightRepository.save(pw));
            }
            List<String> ids = createdWeights.stream().map(ProductWeight::getId).toList();

            entry.setQuantityEntered(quantity);
            entry.setTotalWeight(totalWeight);
            entry.setWeightIds(ids);
            return productEntryRepository.save(entry);
        }

        // Mode fixed weight
        if (product.isHasBaseWeight()) {
            List<ProductWeight> createdWeights = new ArrayList<>();
            for (int i = 0; i < quantity; i++) {
                ProductWeight pw = new ProductWeight();
                pw.setProductId(product.getId());
                pw.setUserId(userId);
                pw.setLocation(loc);
                pw.setWeight(product.getFixedWeight());
                pw.setConsumed(false);
                createdWeights.add(productWeightRepository.save(pw));
            }
            List<String> ids = createdWeights.stream().map(ProductWeight::getId).toList();

            entry.setQuantityEntered(quantity);
            entry.setTotalWeight(product.getFixedWeight() * quantity);
            entry.setWeightIds(ids);
            return productEntryRepository.save(entry);
        }

        // Mode box by box
        if (weights != null && !weights.isEmpty()) {
            // 🔹 Convert safely to Double to avoid ClassCastException (Integer → Double)
            List<Double> safeWeights = weights.stream()
                    .filter(Objects::nonNull)
                    .map(Number::doubleValue)
                    .collect(Collectors.toList());

            List<ProductWeight> createdWeights = new ArrayList<>();
            for (Double w : safeWeights) {
                ProductWeight pw = new ProductWeight();
                pw.setProductId(product.getId());
                pw.setUserId(userId);
                pw.setLocation(loc);
                pw.setWeight(w);
                pw.setConsumed(false);
                createdWeights.add(productWeightRepository.save(pw));
            }
            List<String> ids = createdWeights.stream().map(ProductWeight::getId).toList();

            entry.setQuantityEntered(createdWeights.size());
            entry.setTotalWeight(safeWeights.stream().mapToDouble(Double::doubleValue).sum());
            entry.setWeightIds(ids);
            return productEntryRepository.save(entry);
        }

        return null;
    }
    // --- Exits ---
    public ProductExit registerExit(
            String productId,
            Double manualWeight,
            int quantity,
            List<String> weightIds,
            String userId,
            String notes,
            String location,
            Date customDate // optional parameter
    ) {
        String loc = norm(location);
        if (productId == null) return null;

        Optional<Product> optProduct = productRepository.findById(productId);
        if (optProduct.isEmpty()) return null;
        Product product = optProduct.get();

        ProductExit exit = new ProductExit();
        exit.setProductId(product.getId());
        exit.setProductName(product.getName());
        exit.setBrandId(product.getBrandId());
        exit.setUserId(userId);

        // 👇 use provided date or current
        exit.setExitedAt(customDate != null ? customDate : new Date());

        exit.setNotes(notes);
        exit.setLocation(loc);

        if (product.getSupplierId() != null) {
            exit.setSupplierId(product.getSupplierId());
            if (product.getSupplierName() == null || product.getSupplierName().isBlank()) {
                supplierRepository.findById(product.getSupplierId())
                        .ifPresent(s -> exit.setSupplierName(s.getName()));
            } else {
                exit.setSupplierName(product.getSupplierName());
            }
        }

        if (product.isHasBaseWeight()) {
            List<ProductWeight> available =
                    productWeightRepository.findByProductIdAndUserIdAndLocationAndConsumedFalse(product.getId(), userId, loc);

            if (quantity > available.size()) {
                return null;
            }

            exit.setQuantityExited(quantity);
            double total = product.getFixedWeight() != null ? product.getFixedWeight() * quantity : 0;
            exit.setTotalWeight(total);

            List<ProductWeight> consumed = available.subList(0, quantity);
            consumed.forEach(w -> w.setConsumed(true));
            productWeightRepository.saveAll(consumed);
            exit.setWeightIds(consumed.stream().map(ProductWeight::getId).toList());

        } else if (manualWeight != null && manualWeight > 0) {
            exit.setManualWeight(manualWeight);
            exit.setQuantityExited(1);
            exit.setTotalWeight(manualWeight);

            List<ProductWeight> available =
                    productWeightRepository.findByProductIdAndUserIdAndLocationAndConsumedFalse(product.getId(), userId, loc);
            if (!available.isEmpty()) {
                ProductWeight chosen = available.get(0);
                chosen.setConsumed(true);
                productWeightRepository.save(chosen);
                exit.setWeightIds(List.of(chosen.getId()));
            }

        } else if (weightIds != null && !weightIds.isEmpty()) {
            List<ProductWeight> toConsume = productWeightRepository.findAllById(weightIds)
                    .stream()
                    .filter(w -> loc.equals(w.getLocation()))
                    .toList();
            if (toConsume.isEmpty()) return null;

            double total = toConsume.stream().mapToDouble(ProductWeight::getWeight).sum();
            exit.setQuantityExited(toConsume.size());
            exit.setTotalWeight(total);
            exit.setWeightIds(toConsume.stream().map(ProductWeight::getId).toList());

            toConsume.forEach(w -> w.setConsumed(true));
            productWeightRepository.saveAll(toConsume);

        } else {
            return null;
        }

        return productExitRepository.save(exit);
    }

    // --- Entries & Exits getters / deleters ---
    public List<ProductEntry> getEntries(String userId, String location) {
        String loc = norm(location);
        if (loc != null && !loc.isBlank()) {
            return productEntryRepository.findByUserIdAndLocation(userId, loc);
        }
        return productEntryRepository.findByUserId(userId);
    }

    public boolean deleteEntry(String id) {
        Optional<ProductEntry> entryOpt = productEntryRepository.findById(id);
        if (entryOpt.isPresent()) {
            ProductEntry entry = entryOpt.get();

            if (entry.getWeightIds() != null && !entry.getWeightIds().isEmpty()) {
                List<ProductWeight> weights = productWeightRepository.findAllById(entry.getWeightIds());
                productWeightRepository.deleteAll(weights);
            }

            productEntryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<ProductExit> getExits(String userId, String location) {
        String loc = norm(location);
        if (loc != null && !loc.isBlank()) {
            return productExitRepository.findByUserIdAndLocation(userId, loc);
        }
        return productExitRepository.findByUserId(userId);
    }

    public boolean deleteExit(String id) {
        Optional<ProductExit> exitOpt = productExitRepository.findById(id);
        if (exitOpt.isPresent()) {
            ProductExit exit = exitOpt.get();

            if (exit.getWeightIds() != null && !exit.getWeightIds().isEmpty()) {
                List<ProductWeight> weights = productWeightRepository.findAllById(exit.getWeightIds());
                weights.forEach(w -> w.setConsumed(false));
                productWeightRepository.saveAll(weights);
            }

            productExitRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- NEW: Entries by Date ---
    public List<ProductEntry> getEntriesByDate(String userId, String location, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr); // yyyy-MM-dd
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        if (location != null && !location.isBlank()) {
            return productEntryRepository.findByUserIdAndLocationAndEnteredAtBetween(userId, location, start, end);
        }
        return productEntryRepository.findByUserIdAndEnteredAtBetween(userId, start, end);
    }

    // --- NEW: Exits by Date ---
    public List<ProductExit> getExitsByDate(String userId, String location, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        if (location != null && !location.isBlank()) {
            return productExitRepository.findByUserIdAndLocationAndExitedAtBetween(userId, location, start, end);
        }
        return productExitRepository.findByUserIdAndExitedAtBetween(userId, start, end);
    }

    public boolean clearInventory(String productId, String userId, String location) {
        String loc = norm(location);
        List<ProductEntry> entries = getEntries(userId, loc)
                .stream()
                .filter(e -> e.getProductId().equals(productId))
                .toList();
        productEntryRepository.deleteAll(entries);

        List<ProductExit> exits = getExits(userId, loc)
                .stream()
                .filter(x -> x.getProductId().equals(productId))
                .toList();
        productExitRepository.deleteAll(exits);

        List<ProductWeight> weights = (loc != null && !loc.isBlank())
                ? productWeightRepository.findByProductIdAndUserIdAndLocation(productId, userId, loc)
                : productWeightRepository.findByProductIdAndUserId(productId, userId);

        productWeightRepository.deleteAll(weights);

        return true;
    }

    // --- Summary ---
    public List<ProductSummary> getProductSummary(String userId, String location) {
        String loc = norm(location);
        List<Product> products = productRepository.findByUserId(userId);
        List<ProductSummary> summaries = new ArrayList<>();

        for (Product p : products) {
            List<ProductWeight> remaining = (loc != null && !loc.isBlank())
                    ? productWeightRepository.findByProductIdAndUserIdAndLocationAndConsumedFalse(p.getId(), userId, loc)
                    : productWeightRepository.findByProductIdAndUserIdAndConsumedFalse(p.getId(), userId);

            long totalBoxes = remaining.size();
            double totalWeight = remaining.stream().mapToDouble(ProductWeight::getWeight).sum();

            if (p.isHasBaseWeight() && totalBoxes == 0) {
                long entries = productEntryRepository.findByUserIdAndLocation(userId, loc)
                        .stream()
                        .filter(e -> e.getProductId().equals(p.getId()))
                        .mapToLong(ProductEntry::getQuantityEntered)
                        .sum();

                long exits = productExitRepository.findByUserIdAndLocation(userId, loc)
                        .stream()
                        .filter(x -> x.getProductId().equals(p.getId()))
                        .mapToLong(ProductExit::getQuantityExited)
                        .sum();

                long balance = entries - exits;
                if (balance > 0 && p.getFixedWeight() != null) {
                    totalBoxes = balance;
                    totalWeight = balance * p.getFixedWeight();
                }
            }

            if (totalBoxes > 0 || totalWeight > 0) {
                String supplierName = p.getSupplierName();
                if ((supplierName == null || supplierName.isBlank()) && p.getSupplierId() != null) {
                    supplierName = supplierRepository.findById(p.getSupplierId())
                            .map(Supplier::getName)
                            .orElse("N/A");
                }

                summaries.add(new ProductSummary(
                        p.getId(),
                        p.getName(),
                        p.getBrandId(),
                        p.getSupplierId(),
                        supplierName,
                        p.getCategoryId(),
                        null, // categoryName is filled in controller
                        totalBoxes,
                        totalWeight
                ));
            }
        }

        return summaries;
    }

    // --- DTO with category added ---
    public static class ProductSummary {
        private String productId;
        private String name;
        private String brandId;
        private String supplierId;
        private String supplierName;
        private String categoryId;
        private String categoryName;
        private long totalBoxes;
        private double totalWeight;

        public ProductSummary(String productId, String name, String brandId,
                              String supplierId, String supplierName,
                              String categoryId, String categoryName,
                              long totalBoxes, double totalWeight) {
            this.productId = productId;
            this.name = name;
            this.brandId = brandId;
            this.supplierId = supplierId;
            this.supplierName = supplierName;
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.totalBoxes = totalBoxes;
            this.totalWeight = totalWeight;
        }

        // Getters & setters
        public String getProductId() { return productId; }
        public String getName() { return name; }
        public String getBrandId() { return brandId; }
        public String getSupplierId() { return supplierId; }
        public String getSupplierName() { return supplierName; }
        public String getCategoryId() { return categoryId; }
        public String getCategoryName() { return categoryName; }
        public long getTotalBoxes() { return totalBoxes; }
        public double getTotalWeight() { return totalWeight; }

        public void setProductId(String productId) { this.productId = productId; }
        public void setName(String name) { this.name = name; }
        public void setBrandId(String brandId) { this.brandId = brandId; }
        public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
        public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public void setTotalBoxes(long totalBoxes) { this.totalBoxes = totalBoxes; }
        public void setTotalWeight(double totalWeight) { this.totalWeight = totalWeight; }
    }

    // --- DTO: summary by location ---
    public static class LocationSummary {
        private String location;
        private Totals totals;
        private List<ProductSummary> products;

        public LocationSummary(String location, Totals totals, List<ProductSummary> products) {
            this.location = location;
            this.totals = totals;
            this.products = products;
        }

        public String getLocation() { return location; }
        public Totals getTotals() { return totals; }
        public List<ProductSummary> getProducts() { return products; }
    }

    // --- Inventory summary for all locations ---
    public Map<String, LocationSummary> getInventorySummary(String userId, List<String> locations) {
        Map<String, LocationSummary> result = new LinkedHashMap<>();

        for (String loc : locations) {
            Totals totals = getTotals(userId, loc);
            List<ProductSummary> products = getProductSummary(userId, loc);
            result.put(loc, new LocationSummary(loc, totals, products));
        }

        return result;
    }
}