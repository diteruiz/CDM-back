package com.sgi.inventorysystem.services;

import com.sgi.inventorysystem.models.ProductTemplate;
import com.sgi.inventorysystem.repositories.ProductTemplateRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@Service
public class ProductTemplateService {

    @Autowired
    private ProductTemplateRepository templateRepository;

    // ✅ Get all templates by user
    public List<ProductTemplate> getAllTemplates(String userId) {
        return templateRepository.findByUserId(userId);
    }

    public Optional<ProductTemplate> getTemplateById(String id) {
        return templateRepository.findById(id);
    }

    public List<ProductTemplate> getTemplatesByBrandAndCategory(String brandId, String categoryId, String userId) {
        return templateRepository.findByBrandIdAndCategoryIdAndUserId(brandId, categoryId, userId);
    }

    public ProductTemplate createTemplate(ProductTemplate template) {
        return templateRepository.save(template);
    }

    // ✅ Update template
    public Optional<ProductTemplate> updateTemplate(String id, ProductTemplate updatedTemplate) {
        return templateRepository.findById(id).map(existing -> {
            existing.setName(updatedTemplate.getName());
            existing.setBrandId(updatedTemplate.getBrandId());
            existing.setCategoryId(updatedTemplate.getCategoryId());
            existing.setSupplierId(updatedTemplate.getSupplierId());
            existing.setSupplierName(updatedTemplate.getSupplierName()); // 👈 añadido
            existing.setSupplier(updatedTemplate.getSupplier());         // legacy
            existing.setFixedWeight(updatedTemplate.getFixedWeight());
            existing.setHasBaseWeight(updatedTemplate.isHasBaseWeight());
            existing.setUserId(updatedTemplate.getUserId());
            return templateRepository.save(existing);
        });
    }

    public void deleteTemplate(String id) {
        templateRepository.deleteById(id);
    }

    // ✅ Export templates by user
    public ByteArrayInputStream exportToExcel(String userId) {
        String[] columns = {"ID", "Name", "Brand ID", "Category ID", "Supplier ID", "Supplier Name", "Supplier (Legacy)", "Has Base Weight", "Fixed Weight"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("ProductTemplates");

            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            List<ProductTemplate> templates = templateRepository.findByUserId(userId);
            int rowIdx = 1;

            for (ProductTemplate template : templates) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(template.getId());
                row.createCell(1).setCellValue(template.getName());
                row.createCell(2).setCellValue(template.getBrandId() != null ? template.getBrandId() : "");
                row.createCell(3).setCellValue(template.getCategoryId() != null ? template.getCategoryId() : "");
                row.createCell(4).setCellValue(template.getSupplierId() != null ? template.getSupplierId() : "");
                row.createCell(5).setCellValue(template.getSupplierName() != null ? template.getSupplierName() : "");
                row.createCell(6).setCellValue(template.getSupplier() != null ? template.getSupplier() : "");
                row.createCell(7).setCellValue(template.isHasBaseWeight() ? "YES" : "NO");
                row.createCell(8).setCellValue(template.getFixedWeight() != null ? template.getFixedWeight() : 0.0);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    // ✅ Import templates by user
    public List<ProductTemplate> importFromExcel(MultipartFile file, String userId) {
        List<ProductTemplate> templates = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) rows.next(); // skip header

            while (rows.hasNext()) {
                Row row = rows.next();

                String name = getCellValueAsString(row.getCell(0)).trim();
                String brandId = getCellValueAsString(row.getCell(1)).trim();
                String categoryId = getCellValueAsString(row.getCell(2)).trim();
                String supplierId = getCellValueAsString(row.getCell(3)).trim();
                String supplierName = getCellValueAsString(row.getCell(4)).trim();
                String supplierLegacy = getCellValueAsString(row.getCell(5)).trim();
                String hasBaseWeightStr = getCellValueAsString(row.getCell(6)).trim();
                double fixedWeight = getCellValueAsDouble(row.getCell(7));

                if (name.isEmpty() || brandId.isEmpty() || categoryId.isEmpty()) continue;

                ProductTemplate template = new ProductTemplate();
                template.setName(name);
                template.setBrandId(brandId);
                template.setCategoryId(categoryId);
                template.setSupplierId(!supplierId.isEmpty() ? supplierId : null);
                template.setSupplierName(!supplierName.isEmpty() ? supplierName : null);
                template.setSupplier(!supplierLegacy.isEmpty() ? supplierLegacy : null);
                template.setHasBaseWeight(hasBaseWeightStr.equalsIgnoreCase("YES") || hasBaseWeightStr.equalsIgnoreCase("TRUE"));
                template.setFixedWeight(fixedWeight > 0 ? fixedWeight : null);
                template.setUserId(userId);

                templates.add(template);
            }

            return templateRepository.saveAll(templates);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // --- Helpers ---
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private double getCellValueAsDouble(Cell cell) {
        if (cell == null) return 0.0;
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    yield 0.0;
                }
            }
            default -> 0.0;
        };
    }
}