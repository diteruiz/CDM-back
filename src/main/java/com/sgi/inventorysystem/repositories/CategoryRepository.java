package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    List<Category> findByUserId(String userId);

    List<Category> findByNameIgnoreCase(String name);
}