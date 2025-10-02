package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.ProductTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductTemplateRepository extends MongoRepository<ProductTemplate, String> {
    List<ProductTemplate> findByBrandIdAndCategoryIdAndUserId(String brandId, String categoryId, String userId);
    List<ProductTemplate> findByUserId(String userId);
}