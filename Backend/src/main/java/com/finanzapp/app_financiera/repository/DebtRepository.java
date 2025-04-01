package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.Debt;
import com.finanzapp.app_financiera.models.Saving;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class DebtRepository {
    private final HashMap<String, Debt> tableDebts = new HashMap<>();

    public List<Debt> findAllDebts() {
        return new ArrayList<>(tableDebts.values());
    }

    public Debt save(Debt debt) {
        return tableDebts.put(debt.getId(), debt);
    }

    public Debt remove(String id) {
        return tableDebts.remove(id);
    }

}
