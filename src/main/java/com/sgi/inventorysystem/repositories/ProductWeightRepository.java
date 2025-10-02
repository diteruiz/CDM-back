package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.ProductWeight;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductWeightRepository extends MongoRepository<ProductWeight, String> {

    List<ProductWeight> findByProductIdAndUserId(String productId, String userId);

    List<ProductWeight> findByProductIdAndUserIdAndConsumedFalse(String productId, String userId);

    List<ProductWeight> findByProductIdAndUserIdAndLocationAndConsumedFalse(String productId, String userId, String location);

    List<ProductWeight> findByProductIdAndUserIdAndLocation(String productId, String userId, String location);

    // 👇 Nuevos para Totals en Dashboard
    List<ProductWeight> findByUserId(String userId);

    List<ProductWeight> findByUserIdAndLocation(String userId, String location);
}

