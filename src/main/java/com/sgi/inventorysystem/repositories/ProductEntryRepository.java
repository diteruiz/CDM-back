package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.ProductEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductEntryRepository extends MongoRepository<ProductEntry, String> {

    // 🔹 Para borrar por producto
    List<ProductEntry> findByProductId(String productId);

    // Get all entries that belong to a specific user
    List<ProductEntry> findByUserId(String userId);

    // Get entries filtered by user and location
    List<ProductEntry> findByUserIdAndLocation(String userId, String location);

    // 🔹 NEW: Get entries by user and date range
    List<ProductEntry> findByUserIdAndEnteredAtBetween(
            String userId, LocalDateTime start, LocalDateTime end
    );

    // 🔹 NEW: Get entries by user, location and date range
    List<ProductEntry> findByUserIdAndLocationAndEnteredAtBetween(
            String userId, String location, LocalDateTime start, LocalDateTime end
    );
}