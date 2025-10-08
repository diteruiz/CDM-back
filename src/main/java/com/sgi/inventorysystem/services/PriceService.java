package com.sgi.inventorysystem.services;

import com.sgi.inventorysystem.models.Price;
import com.sgi.inventorysystem.repositories.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PriceService {

    @Autowired
    private PriceRepository priceRepository;

    // Get all prices by user
    public List<Price> getPrices(String userId) {
        return priceRepository.findByUserId(userId);
    }

    // Get prices for a client
    public List<Price> getPricesByClient(String userId, String clientId) {
        return priceRepository.findByUserIdAndClientId(userId, clientId);
    }

    // Create price
    public Price createPrice(Price price) {
        return priceRepository.save(price);
    }

    // Update price
    public Optional<Price> updatePrice(String id, Price updatedPrice) {
        return priceRepository.findById(id).map(price -> {
            price.setProductName(updatedPrice.getProductName());
            price.setPrice(updatedPrice.getPrice());
            price.setClientId(updatedPrice.getClientId());
            return priceRepository.save(price);
        });
    }

    // Delete price
    public void deletePrice(String id) {
        priceRepository.deleteById(id);
    }
}