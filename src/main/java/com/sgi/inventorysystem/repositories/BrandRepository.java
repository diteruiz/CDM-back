package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.Brand;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends MongoRepository<Brand, String> {
    List<Brand> findByUserId(String userId);

    List<Brand> findByNameIgnoreCase(String name);
}