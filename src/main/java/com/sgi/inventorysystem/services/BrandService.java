package com.sgi.inventorysystem.services;

import com.sgi.inventorysystem.models.Brand;
import com.sgi.inventorysystem.repositories.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getAllBrands(String userId) {
        return brandRepository.findByUserId(userId);
    }

    public Optional<Brand> getBrandById(String id) {
        return brandRepository.findById(id);
    }

    public Brand createBrand(Brand brand) {
        brand.setCreatedAt(new Date());
        brand.setUpdatedAt(new Date());
        return brandRepository.save(brand);
    }

    public Brand updateBrand(String id, Brand updatedBrand) {
        return brandRepository.findById(id).map(existing -> {
            existing.setName(updatedBrand.getName());
            existing.setUpdatedAt(new Date());
            return brandRepository.save(existing);
        }).orElse(null);
    }

    public void deleteBrand(String id) {
        brandRepository.deleteById(id);
    }
}