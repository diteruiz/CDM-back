// src/main/java/com/sgi/inventorysystem/services/SaleService.java
package com.sgi.inventorysystem.services;

import com.sgi.inventorysystem.models.Sale;
import com.sgi.inventorysystem.repositories.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    public List<Sale> getSalesByUserId(String userId) {
        return saleRepository.findByUserId(userId);
    }

    public List<Sale> getSalesByUserIdAndDate(String userId, LocalDate date) {
        return saleRepository.findByUserIdAndDate(userId, date);
    }

    public Sale createSale(Sale sale) {
        if (sale.getDate() == null) {
            sale.setDate(LocalDate.now()); // 👈 ahora guarda solo la fecha
        }
        if (sale.getSentWeight() < 0) {
            sale.setSentWeight(0);
        }
        if (sale.getReturnWeight() < 0) {
            sale.setReturnWeight(0);
        }
        return saleRepository.save(sale);
    }

    public Optional<Sale> getSaleById(String id) {
        return saleRepository.findById(id);
    }

    public Sale updateReturn(String id, double returnWeight) {
        Optional<Sale> optSale = saleRepository.findById(id);
        if (optSale.isPresent()) {
            Sale sale = optSale.get();
            if (returnWeight < 0) {
                returnWeight = 0;
            }
            if (returnWeight > sale.getSentWeight()) {
                returnWeight = sale.getSentWeight();
            }
            sale.setReturnWeight(returnWeight);
            return saleRepository.save(sale);
        }
        return null;
    }

    public Sale updateSale(String id, String productName, double sentWeight) {
        Optional<Sale> optSale = saleRepository.findById(id);
        if (optSale.isPresent()) {
            Sale sale = optSale.get();
            sale.setProductName(productName);
            sale.setSentWeight(sentWeight >= 0 ? sentWeight : 0);
            return saleRepository.save(sale);
        }
        return null;
    }

    public void deleteSale(String id) {
        saleRepository.deleteById(id);
    }
}