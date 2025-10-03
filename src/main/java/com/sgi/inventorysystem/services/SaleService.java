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
            sale.setDate(LocalDate.now());
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

    // ✅ Updated: full update (replace all fields, including arrays)
    public Sale updateSale(String id,
                           String productName,
                           double sentWeight,
                           double returnWeight,
                           List<Double> barrels,
                           List<Double> returnBarrels) {
        Optional<Sale> optSale = saleRepository.findById(id);
        if (optSale.isPresent()) {
            Sale sale = optSale.get();

            sale.setProductName(productName);
            sale.setSentWeight(sentWeight >= 0 ? sentWeight : 0);

            if (returnWeight < 0) {
                returnWeight = 0;
            }
            if (returnWeight > sale.getSentWeight()) {
                returnWeight = sale.getSentWeight();
            }
            sale.setReturnWeight(returnWeight);

            // 👉 Replace arrays instead of merging
            sale.setBarrels(barrels != null ? barrels : List.of());
            sale.setReturnBarrels(returnBarrels != null ? returnBarrels : List.of());

            return saleRepository.save(sale);
        }
        return null;
    }

    public void deleteSale(String id) {
        saleRepository.deleteById(id);
    }
}