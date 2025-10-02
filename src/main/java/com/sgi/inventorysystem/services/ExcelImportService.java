package com.sgi.inventorysystem.services;

import com.sgi.inventorysystem.models.Product;
import com.sgi.inventorysystem.models.Category;
import com.sgi.inventorysystem.models.Brand;
import com.sgi.inventorysystem.models.Supplier;
import com.sgi.inventorysystem.repositories.ProductRepository;
import com.sgi.inventorysystem.repositories.CategoryRepository;
import com.sgi.inventorysystem.repositories.BrandRepository;
import com.sgi.inventorysystem.repositories.SupplierRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class ExcelImportService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    // ✅ Import products from Excel file with userId (brand/category names instead of IDs)
    public List<Product> importProductsFromExcel(MultipartFile file, String userId) {
        List<Product> products = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) rows.next(); // Skip header row

            while (rows.hasNext()) {
                Row row = rows.next();

                String name = getCellValueAsString(row.getCell(0)).trim();       // Product Name
                String brandName = getCellValueAsString(row.getCell(1)).trim(); // Brand Name
                String categoryName = getCellValueAsString(row.getCell(2)).trim(); // Category Name
                Double fixedWeight = getCellValueAsDouble(row.getCell(3));      // Fixed Weight (optional)
                boolean hasBaseWeight = fixedWeight != null && fixedWeight > 0;

                if (name.isEmpty() || brandName.isEmpty() || categoryName.isEmpty()) continue;

                // Look up brand by name
                Optional<Brand> brandOpt = brandRepository.findByNameIgnoreCase(brandName).stream().findFirst();
                if (brandOpt.isEmpty()) continue;

                // Look up category by name
                Optional<Category> categoryOpt = categoryRepository.findByNameIgnoreCase(categoryName).stream().findFirst();
                if (categoryOpt.isEmpty()) continue;

                Product product = new Product();
                product.setName(name);
                product.setBrandId(brandOpt.get().getId());
                product.setCategoryId(categoryOpt.get().getId());
                product.setFixedWeight(hasBaseWeight ? fixedWeight : null);
                product.setHasBaseWeight(hasBaseWeight);
                product.setCreatedAt(new Date());
                product.setUpdatedAt(new Date());
                product.setUserId(userId);

                products.add(product);
            }

            return productRepository.saveAll(products);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ Import categories
    public List<Category> importCategoriesFromExcel(MultipartFile file, String userId) {
        List<Category> categories = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) rows.next(); // Skip header

            while (rows.hasNext()) {
                Row row = rows.next();

                String name = getCellValueAsString(row.getCell(0)).trim();
                String description = getCellValueAsString(row.getCell(1)).trim();

                if (name.isEmpty()) continue;

                Category category = new Category();
                category.setName(name);
                category.setDescription(description);
                category.setCreatedAt(new Date());
                category.setUserId(userId);

                categories.add(category);
            }

            return categoryRepository.saveAll(categories);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ Import brands
    public List<Brand> importBrandsFromExcel(MultipartFile file, String userId) {
        List<Brand> brands = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) rows.next(); // Skip header

            while (rows.hasNext()) {
                Row row = rows.next();

                String name = getCellValueAsString(row.getCell(0)).trim();

                if (name.isEmpty()) continue;

                Brand brand = new Brand();
                brand.setName(name);
                brand.setUserId(userId);

                brands.add(brand);
            }

            return brandRepository.saveAll(brands);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ Import suppliers
    public List<Supplier> importSuppliersFromExcel(MultipartFile file, String userId) {
        List<Supplier> suppliers = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) rows.next(); // Skip header

            while (rows.hasNext()) {
                Row row = rows.next();

                String name = getCellValueAsString(row.getCell(0)).trim();

                if (name.isEmpty()) continue;

                Supplier supplier = new Supplier();
                supplier.setName(name);
                supplier.setUserId(userId);

                suppliers.add(supplier);
            }

            return supplierRepository.saveAll(suppliers);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }
}