package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.Price;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PriceRepository extends MongoRepository<Price, String> {
    List<Price> findByUserId(String userId);
    List<Price> findByUserIdAndClientId(String userId, String clientId);
}