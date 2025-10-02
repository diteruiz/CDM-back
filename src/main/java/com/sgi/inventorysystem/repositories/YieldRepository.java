package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.Yield;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface YieldRepository extends MongoRepository<Yield, String> {
    List<Yield> findByUserIdAndDate(String userId, String date);
    List<Yield> findByUserId(String userId);
}