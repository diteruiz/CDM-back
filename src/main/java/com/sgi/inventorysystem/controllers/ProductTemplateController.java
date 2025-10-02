package com.sgi.inventorysystem.controllers;

import com.sgi.inventorysystem.models.ProductTemplate;
import com.sgi.inventorysystem.services.ProductTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product-templates")
@CrossOrigin(origins = "*")
public class ProductTemplateController {

    @Autowired
    private ProductTemplateService templateService;

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public List<ProductTemplate> getAllTemplates() {
        return templateService.getAllTemplates(getUserId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public Optional<ProductTemplate> getTemplateById(@PathVariable String id) {
        return templateService.getTemplateById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/search")
    public List<ProductTemplate> getByBrandAndCategory(
            @RequestParam String brandId,
            @RequestParam String categoryId) {
        return templateService.getTemplatesByBrandAndCategory(brandId, categoryId, getUserId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ProductTemplate createTemplate(@RequestBody ProductTemplate template) {
        template.setUserId(getUserId());
        return templateService.createTemplate(template);
    }

    // ✅ Método agregado para actualizar plantillas
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductTemplate> updateTemplate(
            @PathVariable String id,
            @RequestBody ProductTemplate template) {
        template.setUserId(getUserId());
        return templateService.updateTemplate(id, template)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public void deleteTemplate(@PathVariable String id) {
        templateService.deleteTemplate(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/import")
    public ResponseEntity<List<ProductTemplate>> importTemplates(@RequestParam("file") MultipartFile file) {
        List<ProductTemplate> imported = templateService.importFromExcel(file, getUserId());
        if (imported.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(imported);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/export")
    public void exportTemplates(HttpServletResponse response) {
        try {
            ByteArrayInputStream stream = templateService.exportToExcel(getUserId());
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=product_templates.xlsx");
            response.getOutputStream().write(stream.readAllBytes());
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}