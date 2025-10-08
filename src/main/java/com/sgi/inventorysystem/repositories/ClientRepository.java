package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClientRepository extends MongoRepository<Client, String> {
    List<Client> findByUserId(String userId);
}