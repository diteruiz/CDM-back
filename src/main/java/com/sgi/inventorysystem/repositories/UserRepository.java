package com.sgi.inventorysystem.repositories;

import com.sgi.inventorysystem.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

// This interface allows basic CRUD operations for the User collection
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
}
