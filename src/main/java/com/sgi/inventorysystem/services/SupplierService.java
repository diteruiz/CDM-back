package com.sgi.inventorysystem.services;

import com.sgi.inventorysystem.models.Supplier;
import com.sgi.inventorysystem.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public List<Supplier> getSuppliers(String userId) {
        return supplierRepository.findByUserId(userId);
    }

    public Optional<Supplier> getSupplierById(String id) {
        return supplierRepository.findById(id);
    }

    public Supplier createSupplier(Supplier supplier) {
        supplier.setCreatedAt(new Date());
        supplier.setUpdatedAt(new Date());
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(String id, Supplier updatedSupplier) {
        return supplierRepository.findById(id).map(s -> {
            s.setName(updatedSupplier.getName());
            s.setUpdatedAt(new Date());
            return supplierRepository.save(s);
        }).orElse(null);
    }

    public void deleteSupplier(String id) {
        supplierRepository.deleteById(id);
    }
}