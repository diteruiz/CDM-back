package com.sgi.inventorysystem.config;

import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    // Specifies the name of the MongoDB database to use
    @Override
    protected String getDatabaseName() {
        return "sgi_carnicos";
    }

    // Creates a MongoClient bean connected to the local MongoDB server
    @Bean
    @Override
    public com.mongodb.client.MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    // Provides a MongoTemplate bean for interacting with MongoDB
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
