package com.sgi.inventorysystem.controllers;

import com.sgi.inventorysystem.models.Sale;
import com.sgi.inventorysystem.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<List<Sale>> getSales(
            Principal principal,
            @RequestParam(required = false) String date
    ) {
        String userId = principal.getName();

        if (date != null) {
            LocalDate localDate = LocalDate.parse(date); // yyyy-MM-dd
            return ResponseEntity.ok(saleService.getSalesByUserIdAndDate(userId, localDate));
        }

        return ResponseEntity.ok(saleService.getSalesByUserId(userId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<Sale> createSale(@RequestBody Sale sale, Principal principal) {
        sale.setUserId(principal.getName());

        if (sale.getDate() == null) {
            sale.setDate(LocalDate.now());
        }
        if (sale.getSentWeight() < 0) {
            sale.setSentWeight(0);
        }
        if (sale.getReturnWeight() < 0) {
            sale.setReturnWeight(0);
        }

        return ResponseEntity.ok(saleService.createSale(sale));
    }

    // 👇 Updated: now saves both returnWeight and returnBarrels
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}/return")
    public ResponseEntity<Sale> updateReturn(@PathVariable String id, @RequestBody Sale payload) {
        Sale updated = saleService.updateReturn(
                id,
                payload.getReturnWeight(),
                payload.getReturnBarrels()
        );
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Sale> updateSale(@PathVariable String id, @RequestBody Sale payload) {
        Sale updated = saleService.updateSale(id, payload.getProductName(), payload.getSentWeight());
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable String id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }
}