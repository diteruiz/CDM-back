package com.sgi.inventorysystem.controllers;

import com.sgi.inventorysystem.models.Brand;
import com.sgi.inventorysystem.services.BrandService;
import com.sgi.inventorysystem.services.ExcelExportService;
import com.sgi.inventorysystem.services.ExcelImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/brands")
@CrossOrigin(origins = "*")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private ExcelImportService excelImportService;

    private String getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<Brand>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands(getUserId()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable String id) {
        Optional<Brand> brand = brandService.getBrandById(id);
        return brand.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<Brand> createBrand(@RequestBody Brand brand) {
        brand.setUserId(getUserId());
        return ResponseEntity.ok(brandService.createBrand(brand));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable String id, @RequestBody Brand brand) {
        Brand updated = brandService.updateBrand(id, brand);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable String id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/import")
    public ResponseEntity<List<Brand>> importBrands(@RequestParam("file") MultipartFile file) {
        List<Brand> imported = excelImportService.importBrandsFromExcel(file, getUserId());
        if (imported.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(imported);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportBrands() {
        try {
            ByteArrayInputStream stream = excelExportService.exportBrandsToExcel(getUserId());
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=brands.xlsx")
                    .body(stream.readAllBytes());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}