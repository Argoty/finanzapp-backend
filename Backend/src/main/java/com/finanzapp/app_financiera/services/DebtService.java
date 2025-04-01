package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.Debt;
import com.finanzapp.app_financiera.models.Saving;
import com.finanzapp.app_financiera.repository.DebtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebtService {
    private final DebtRepository debtRepository;

    @Autowired
    public DebtService(DebtRepository debtRepository) {
        this.debtRepository = debtRepository;
    }

    public void initDebts(){
        
    }

    public List<Debt> findAllDebts() {
        return debtRepository.findAllDebts();
    }

    public Debt save(Debt debt) {
        return debtRepository.save(debt);
    }

    public Debt deleteDebtById(String id) {
        return debtRepository.remove(id);
    }
}
