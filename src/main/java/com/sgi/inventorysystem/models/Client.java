package com.sgi.inventorysystem.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "clients")
public class Client {
    @Id
    private String id;
    private String userId; // to filter clients by logged user
    private String name;
    private String contact;
    private String address;
    private String notes;

    // Constructors
    public Client() {}

    public Client(String userId, String name, String contact, String address, String notes) {
        this.userId = userId;
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.notes = notes;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}