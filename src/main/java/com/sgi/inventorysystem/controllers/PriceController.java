package com.sgi.inventorysystem.controllers;

import com.sgi.inventorysystem.models.Price;
import com.sgi.inventorysystem.services.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/prices")
@CrossOrigin(origins = "*")
public class PriceController {

    @Autowired
    private PriceService priceService;

    // Get all prices for logged user
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<List<Price>> getPrices(Principal principal) {
        String userId = principal.getName();
        return ResponseEntity.ok(priceService.getPrices(userId));
    }

    // Get prices by client
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Price>> getPricesByClient(Principal principal, @PathVariable String clientId) {
        String userId = principal.getName();
        return ResponseEntity.ok(priceService.getPricesByClient(userId, clientId));
    }

    // Create price
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<Price> createPrice(@RequestBody Price price, Principal principal) {
        price.setUserId(principal.getName());
        return ResponseEntity.ok(priceService.createPrice(price));
    }

    // Update price
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Price> updatePrice(@PathVariable String id, @RequestBody Price price) {
        Optional<Price> updated = priceService.updatePrice(id, price);
        return updated.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Delete price
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrice(@PathVariable String id) {
        priceService.deletePrice(id);
        return ResponseEntity.noContent().build();
    }
}