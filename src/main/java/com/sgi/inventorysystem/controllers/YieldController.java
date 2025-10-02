package com.sgi.inventorysystem.controllers;

import com.sgi.inventorysystem.models.Yield;
import com.sgi.inventorysystem.services.YieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/yields")
@CrossOrigin(origins = "*")
public class YieldController {

    @Autowired
    private YieldService yieldService;

    // Obtener todos los yields de un usuario (opcionalmente filtrados por fecha)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<List<Yield>> getYields(
            Principal principal,
            @RequestParam(required = false) String date
    ) {
        String userId = principal.getName();
        if (date != null) {
            return ResponseEntity.ok(yieldService.getYieldsByUserAndDate(userId, date));
        } else {
            return ResponseEntity.ok(yieldService.getAllYieldsByUser(userId));
        }
    }

    // Crear nuevo yield
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<Yield> createYield(@RequestBody Yield yield, Principal principal) {
        String userId = principal.getName();
        yield.setUserId(userId);
        return ResponseEntity.ok(yieldService.saveYield(yield));
    }

    // Actualizar yield existente
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Yield> updateYield(@PathVariable String id, @RequestBody Yield updatedYield, Principal principal) {
        Optional<Yield> existing = yieldService.getYieldById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Yield yield = existing.get();

        if (!yield.getUserId().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        yield.setDate(updatedYield.getDate());
        yield.setBaseWeight(updatedYield.getBaseWeight());
        yield.setBoxes(updatedYield.getBoxes());
        yield.setTotalBase(updatedYield.getTotalBase());
        yield.setTotalObtained(updatedYield.getTotalObtained());
        yield.setRendimiento(updatedYield.getRendimiento());

        return ResponseEntity.ok(yieldService.saveYield(yield));
    }

    // Eliminar yield
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteYield(@PathVariable String id, Principal principal) {
        Optional<Yield> existing = yieldService.getYieldById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Yield yield = existing.get();

        if (!yield.getUserId().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        yieldService.deleteYield(id);
        return ResponseEntity.noContent().build();
    }
}