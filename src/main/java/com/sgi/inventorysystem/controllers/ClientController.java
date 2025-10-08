package com.sgi.inventorysystem.controllers;

import com.sgi.inventorysystem.models.Client;
import com.sgi.inventorysystem.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clients")
@CrossOrigin(origins = "*")
public class ClientController {

    @Autowired
    private ClientService clientService;

    // Get all clients for logged user
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<List<Client>> getClients(Principal principal) {
        String userId = principal.getName();
        return ResponseEntity.ok(clientService.getClients(userId));
    }

    // Create client
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client, Principal principal) {
        client.setUserId(principal.getName());
        return ResponseEntity.ok(clientService.createClient(client));
    }

    // Update client
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable String id, @RequestBody Client client) {
        Optional<Client> updated = clientService.updateClient(id, client);
        return updated.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Delete client
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable String id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}