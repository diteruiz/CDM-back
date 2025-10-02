package com.sgi.inventorysystem.controllers;

import com.sgi.inventorysystem.models.Supplier;
import com.sgi.inventorysystem.services.SupplierService;
import com.sgi.inventorysystem.services.ExcelExportService;
import com.sgi.inventorysystem.services.ExcelImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/suppliers")
@CrossOrigin(origins = "*")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private ExcelImportService excelImportService;

    // Get all suppliers by user
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public List<Supplier> getSuppliers(Principal principal) {
        return supplierService.getSuppliers(principal.getName());
    }

    // Create supplier
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public Supplier createSupplier(@RequestBody Supplier supplier, Principal principal) {
        supplier.setUserId(principal.getName());
        return supplierService.createSupplier(supplier);
    }

    // Update supplier
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable String id, @RequestBody Supplier supplier) {
        Supplier updated = supplierService.updateSupplier(id, supplier);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // Delete supplier
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable String id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok().build();
    }

    // Export suppliers to Excel
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportSuppliers(Principal principal) throws IOException {
        ByteArrayInputStream in = excelExportService.exportSuppliersToExcel(principal.getName());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=suppliers.xlsx")
                .body(in.readAllBytes());
    }

    // Import suppliers from Excel
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/import")
    public ResponseEntity<String> importSuppliers(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            excelImportService.importSuppliersFromExcel(file, principal.getName());
            return ResponseEntity.ok("Suppliers imported successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to import suppliers: " + e.getMessage());
        }
    }
}