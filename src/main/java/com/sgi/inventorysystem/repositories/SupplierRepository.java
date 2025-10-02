package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.Supplier;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SupplierRepository extends MongoRepository<Supplier, String> {
    List<Supplier> findByUserId(String userId);
}