// src/main/java/com/sgi/inventorysystem/repositories/SaleRepository.java
package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.Sale;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface SaleRepository extends MongoRepository<Sale, String> {
    List<Sale> findByUserId(String userId);

    // Buscar por fecha exacta con LocalDate
    List<Sale> findByUserIdAndDate(String userId, LocalDate date);
}