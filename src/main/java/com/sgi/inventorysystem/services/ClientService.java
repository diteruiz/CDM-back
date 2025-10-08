package com.sgi.inventorysystem.services;

import com.sgi.inventorysystem.models.Client;
import com.sgi.inventorysystem.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    // Get all clients by user
    public List<Client> getClients(String userId) {
        return clientRepository.findByUserId(userId);
    }

    // Create client
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    // Update client
    public Optional<Client> updateClient(String id, Client updatedClient) {
        return clientRepository.findById(id).map(client -> {
            client.setName(updatedClient.getName());
            client.setContact(updatedClient.getContact());
            client.setAddress(updatedClient.getAddress());
            client.setNotes(updatedClient.getNotes());
            return clientRepository.save(client);
        });
    }

    // Delete client
    public void deleteClient(String id) {
        clientRepository.deleteById(id);
    }
}