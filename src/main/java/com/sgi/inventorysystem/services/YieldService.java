package com.sgi.inventorysystem.services;

import com.sgi.inventorysystem.models.Yield;
import com.sgi.inventorysystem.repositories.YieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class YieldService {

    @Autowired
    private YieldRepository yieldRepository;

    public List<Yield> getYieldsByUserAndDate(String userId, String date) {
        return yieldRepository.findByUserIdAndDate(userId, date);
    }

    public List<Yield> getAllYieldsByUser(String userId) {
        return yieldRepository.findByUserId(userId);
    }

    public Yield saveYield(Yield yield) {
        return yieldRepository.save(yield);
    }

    public void deleteYield(String id) {
        yieldRepository.deleteById(id);
    }

    public Optional<Yield> getYieldById(String id) {
        return yieldRepository.findById(id);
    }
}