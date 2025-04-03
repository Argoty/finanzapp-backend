package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.Saving;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class SavingRepository {
    private final Map<String, Saving> tableSaving = new HashMap<>();

    public List<Saving> findAllSavings(String userId) {
        return new ArrayList<>(tableSaving.values().stream()
                .filter(e -> e.getUserId().equals(userId))
                .collect(Collectors.toList()));
    }

    public Saving save(Saving saving) {
        tableSaving.put(saving.getId(), saving);
        return saving;
    }

    public Saving remove(String id) {
        return tableSaving.remove(id);
    }

    public Saving findSavingById(String id) {
        return tableSaving.get(id);
    }
}
