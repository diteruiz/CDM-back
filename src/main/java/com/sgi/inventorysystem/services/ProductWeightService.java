package com.sgi.inventorysystem.services;

import com.sgi.inventorysystem.models.ProductWeight;
import com.sgi.inventorysystem.repositories.ProductWeightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductWeightService {

    @Autowired
    private ProductWeightRepository productWeightRepository;

    // --- Create weight record (entrada de caja con peso variable) ---
    public ProductWeight saveWeight(ProductWeight weight) {
        if (weight.getCreatedAt() == null) {
            weight.setCreatedAt(new Date());
        }
        weight.setConsumed(false); // always available at creation
        return productWeightRepository.save(weight);
    }

    // --- Get all weights (includes consumed) ---
    public List<ProductWeight> getWeightsByProduct(String productId, String userId) {
        return productWeightRepository.findByProductIdAndUserId(productId, userId);
    }

    // --- Get only available weights (not consumed) ---
    public List<ProductWeight> getAvailableWeights(String productId, String userId) {
        return productWeightRepository.findByProductIdAndUserIdAndConsumedFalse(productId, userId);
    }

    // --- Consume N weights (for exits) ---
    public List<ProductWeight> consumeWeights(String productId, int quantity, String userId) {
        List<ProductWeight> available = productWeightRepository.findByProductIdAndUserIdAndConsumedFalse(productId, userId);

        if (available.isEmpty()) return List.of();

        List<ProductWeight> toConsume = available.stream().limit(quantity).toList();
        toConsume.forEach(pw -> pw.setConsumed(true));
        return productWeightRepository.saveAll(toConsume);
    }

    // --- Helpers ---
    public double getTotalWeightByProduct(String productId, String userId) {
        return productWeightRepository.findByProductIdAndUserIdAndConsumedFalse(productId, userId)
                .stream()
                .mapToDouble(ProductWeight::getWeight)
                .sum();
    }

    public long getTotalBoxesByProduct(String productId, String userId) {
        return productWeightRepository.findByProductIdAndUserIdAndConsumedFalse(productId, userId).size();
    }

    public Optional<ProductWeight> getById(String id) {
        return productWeightRepository.findById(id);
    }

    public void deleteById(String id) {
        productWeightRepository.deleteById(id);
    }

    // --- Optional: reset consumed weights (debug/testing) ---
    public void resetConsumed(String productId, String userId) {
        List<ProductWeight> all = productWeightRepository.findByProductIdAndUserId(productId, userId);
        all.forEach(pw -> pw.setConsumed(false));
        productWeightRepository.saveAll(all);
    }
}