package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.ProductExit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductExitRepository extends MongoRepository<ProductExit, String> {

    // 🔹 Para borrar por producto
    List<ProductExit> findByProductId(String productId);

    // Get all exits that belong to a specific user
    List<ProductExit> findByUserId(String userId);

    // Get exits filtered by user and location
    List<ProductExit> findByUserIdAndLocation(String userId, String location);

    // 🔹 NEW: Get exits by user and date range
    List<ProductExit> findByUserIdAndExitedAtBetween(
            String userId, LocalDateTime start, LocalDateTime end
    );

    // 🔹 NEW: Get exits by user, location and date range
    List<ProductExit> findByUserIdAndLocationAndExitedAtBetween(
            String userId, String location, LocalDateTime start, LocalDateTime end
    );
}