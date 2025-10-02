package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.ProductExit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductExitRepository extends MongoRepository<ProductExit, String> {

    // 🔹 Nuevo para borrar por producto
    List<ProductExit> findByProductId(String productId);

    // Get all exits that belong to a specific user
    List<ProductExit> findByUserId(String userId);

    // Get exits filtered by user and location
    List<ProductExit> findByUserIdAndLocation(String userId, String location);
}