package com.sgi.inventorysystem.services;

import com.sgi.inventorysystem.models.Product;
import com.sgi.inventorysystem.models.Brand;
import com.sgi.inventorysystem.models.Category;
import com.sgi.inventorysystem.models.Supplier;
import com.sgi.inventorysystem.repositories.ProductRepository;
import com.sgi.inventorysystem.repositories.BrandRepository;
import com.sgi.inventorysystem.repositories.CategoryRepository;
import com.sgi.inventorysystem.repositories.SupplierRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    // ✅ Export products with Brand and Category names
    public ByteArrayInputStream exportProductsToExcel(String userId) throws IOException {
        String[] columns = {
                "ID", "Name", "Brand Name", "Category Name",
                "Fixed Weight", "Has Base Weight", "Created At", "Updated At"
        };

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Products");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            List<Product> products = productRepository.findByUserId(userId);
            int rowIdx = 1;

            for (Product product : products) {
                Row row = sheet.createRow(rowIdx++);

                String brandName = product.getBrandId() != null ?
                        brandRepository.findById(product.getBrandId()).map(Brand::getName).orElse("N/A") : "N/A";

                String categoryName = product.getCategoryId() != null ?
                        categoryRepository.findById(product.getCategoryId()).map(Category::getName).orElse("N/A") : "N/A";

                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(brandName);
                row.createCell(3).setCellValue(categoryName);
                row.createCell(4).setCellValue(product.getFixedWeight() != null ? product.getFixedWeight() : 0.0);
                row.createCell(5).setCellValue(product.isHasBaseWeight() ? "YES" : "NO");
                row.createCell(6).setCellValue(product.getCreatedAt().toString());
                row.createCell(7).setCellValue(product.getUpdatedAt().toString());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // ✅ Export brands
    public ByteArrayInputStream exportBrandsToExcel(String userId) throws IOException {
        String[] columns = {"ID", "Name"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Brands");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            List<Brand> brands = brandRepository.findByUserId(userId);
            int rowIdx = 1;

            for (Brand brand : brands) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(brand.getId());
                row.createCell(1).setCellValue(brand.getName());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // ✅ Export categories
    public ByteArrayInputStream exportCategoriesToExcel(String userId) throws IOException {
        String[] columns = {"ID", "Name", "Description"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Categories");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            List<Category> categories = categoryRepository.findByUserId(userId);
            int rowIdx = 1;

            for (Category category : categories) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(category.getId());
                row.createCell(1).setCellValue(category.getName());
                row.createCell(2).setCellValue(category.getDescription() != null ? category.getDescription() : "");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // ✅ Export suppliers
    public ByteArrayInputStream exportSuppliersToExcel(String userId) throws IOException {
        String[] columns = {"ID", "Name"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Suppliers");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            List<Supplier> suppliers = supplierRepository.findByUserId(userId);
            int rowIdx = 1;

            for (Supplier supplier : suppliers) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(supplier.getId());
                row.createCell(1).setCellValue(supplier.getName());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}