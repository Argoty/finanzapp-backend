package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.Debt;
import com.finanzapp.app_financiera.models.Saving;
import com.finanzapp.app_financiera.repository.DebtRepository;
import com.finanzapp.app_financiera.repository.UserRepository;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DebtService {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;

    @Autowired
    public DebtService(DebtRepository debtRepository, UserRepository userRepository) {
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
        initDebts();
    }

    public void initDebts() {
        save(new Debt("321", "pepe", 0, 10, LocalDate.of(2025, 6, 2)));
    }

    public List<Debt> findAllDebts(String userId) {
        if (userRepository.findById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        return debtRepository.findAllDebts(userId);
    }

    public Debt save(Debt debt) {
        return debtRepository.save(debt);
    }

    public Debt deleteDebtById(String id) {
        if (debtRepository.findById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Deuda no encontrada");
        }
        return debtRepository.remove(id);
    }

    public Debt updateDebt(Debt debt, String userId, String id) {
        if (debtRepository.findById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Deuda no encontrada");
        }
        if (userRepository.findById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        debt.setId(id);
        return debtRepository.save(debt);
    }

    public Debt payDebt(String id, String userId, double payment) {
        if (debtRepository.findById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Deuda no encontrada");
        }
        if (userRepository.findById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        Debt debt = debtRepository.findById(id);
        debt.setAccumulatedAmount(payment + debt.getAccumulatedAmount());
        return debt;
    }
}
