package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByCategoryId(String categoryId);

    List<Product> findByBrandId(String brandId);

    List<Product> findByUserId(String userId);

    // 🔹 Get products filtered by user and location
    List<Product> findByUserIdAndLocation(String userId, String location);
}