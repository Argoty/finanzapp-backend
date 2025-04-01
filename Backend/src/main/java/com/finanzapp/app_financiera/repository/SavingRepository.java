package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.Saving;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SavingRepository {
    private final Map<String, Saving> tableSaving = new HashMap<>();

    public List<Saving> findAllSavings() {
        return new ArrayList<>(tableSaving.values());
    }

    public Saving save(Saving saving) {
        return tableSaving.put(saving.getId(), saving);
    }

    public Saving remove(String id) {
        return tableSaving.remove(id);
    }

    public Saving findSavingById(String id) {
        return tableSaving.get(id);
    }
}
