package com.sgi.inventorysystem.controllers;

import com.sgi.inventorysystem.models.ProductWeight;
import com.sgi.inventorysystem.services.ProductWeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product-weights")
@CrossOrigin(origins = "*")
public class ProductWeightController {

    @Autowired
    private ProductWeightService productWeightService;

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // --- Create ---
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<ProductWeight> addWeight(@RequestBody ProductWeight weight) {
        weight.setUserId(getUserId()); // asignar usuario autenticado
        weight.setConsumed(false);     // nuevo registro = disponible
        return ResponseEntity.ok(productWeightService.saveWeight(weight));
    }

    // --- Read ---
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductWeight>> getWeights(@PathVariable String productId) {
        return ResponseEntity.ok(productWeightService.getWeightsByProduct(productId, getUserId()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/totals/{productId}")
    public ResponseEntity<TotalsResponse> getTotals(@PathVariable String productId) {
        double totalWeight = productWeightService.getTotalWeightByProduct(productId, getUserId());
        long totalBoxes = productWeightService.getTotalBoxesByProduct(productId, getUserId());
        return ResponseEntity.ok(new TotalsResponse(totalBoxes, totalWeight));
    }

    // --- Update ---
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductWeight> updateWeight(
            @PathVariable String id,
            @RequestBody ProductWeight updatedWeight) {

        Optional<ProductWeight> existing = productWeightService.getById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProductWeight weight = existing.get();

        // Validar que pertenece al usuario autenticado
        if (!weight.getUserId().equals(getUserId())) {
            return ResponseEntity.status(403).build();
        }

        weight.setWeight(updatedWeight.getWeight()); // solo actualiza peso
        return ResponseEntity.ok(productWeightService.saveWeight(weight));
    }

    // --- Consume (Exit) ---
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}/consume")
    public ResponseEntity<ProductWeight> consumeWeight(@PathVariable String id) {
        Optional<ProductWeight> existing = productWeightService.getById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProductWeight weight = existing.get();

        // Validar que pertenece al usuario autenticado
        if (!weight.getUserId().equals(getUserId())) {
            return ResponseEntity.status(403).build();
        }

        // Ya estaba consumido
        if (weight.isConsumed()) {
            return ResponseEntity.badRequest().build();
        }

        weight.setConsumed(true);
        return ResponseEntity.ok(productWeightService.saveWeight(weight));
    }

    // --- Delete ---
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeight(@PathVariable String id) {
        Optional<ProductWeight> existing = productWeightService.getById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Validar que pertenece al usuario autenticado
        if (!existing.get().getUserId().equals(getUserId())) {
            return ResponseEntity.status(403).build();
        }

        productWeightService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Helper DTO ---
    static class TotalsResponse {
        public long totalBoxes;
        public double totalWeight;

        public TotalsResponse(long totalBoxes, double totalWeight) {
            this.totalBoxes = totalBoxes;
            this.totalWeight = totalWeight;
        }
    }
}