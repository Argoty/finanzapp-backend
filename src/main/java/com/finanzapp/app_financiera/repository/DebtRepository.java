package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.Debt;
import com.finanzapp.app_financiera.models.Saving;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DebtRepository {
    private final HashMap<String, Debt> tableDebts = new HashMap<>();

    public List<Debt> findAllDebts(String userId) {
        return new ArrayList<>(tableDebts.values().stream()
                .filter(e -> e.getUserId().equals(userId))
                .collect(Collectors.toList()));
    }
    
    public Debt findById(String id) {
        return tableDebts.get(id);
    }

    public Debt save(Debt debt) {
        tableDebts.put(debt.getId(), debt);
        return debt;
    }

    public Debt remove(String id) {
        return tableDebts.remove(id);
    }
    

}
