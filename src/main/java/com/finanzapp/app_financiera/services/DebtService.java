package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.Debt;
import com.finanzapp.app_financiera.models.Saving;
import com.finanzapp.app_financiera.repository.DebtRepository;
import com.finanzapp.app_financiera.repository.UserRepository;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        /*save(new Debt("1","321", "Casa", 0, 10, LocalDate.of(2025, 6, 2)));
        save(new Debt("2","321", "Auto", 500, 5000, LocalDate.now().plusDays(7)));
        save(new Debt("3","321", "Crédito personal", 200, 2000, LocalDate.now().plusDays(20)));
        save(new Debt("4","321", "Estudios", 0, 10000, LocalDate.now().plusDays(29)));
        save(new Debt("5","321", "Vacaciones", 300, 3000, LocalDate.now().plusDays(14)));
        save(new Debt("6","321", "Muebles", 150, 1500, LocalDate.now().plusDays(2)));
        save(new Debt("7","321", "Electrodomésticos", 100, 1200, LocalDate.now().plusDays(12)));
        save(new Debt("8","321", "Médico", 0, 2500, LocalDate.now().plusDays(5)));
        save(new Debt("9","321", "Reparaciones", 400, 4000, LocalDate.now().plusDays(27)));
        save(new Debt("10","321", "Negocio", 1000, 15000, LocalDate.now().minusDays(10)));*/
    }

    public List<Debt> findAllDebts(String userId) {
        if (userRepository.findById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        return debtRepository.findAllByUserId(userId);
    }

    public Debt save(Debt debt) {
        return debtRepository.save(debt);
    }

    public Debt deleteDebtById(String id) {
        Optional<Debt> debt = debtRepository.findById(id);
        debt.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deuda no encontrada"));

        debtRepository.deleteById(id);
        return debt.get();
    }

    public Debt updateDebt(Debt debt, String userId, String id) {
        debtRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deuda no encontrada"));

        if (userRepository.findById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        debt.setId(id);
        return debtRepository.save(debt);
    }

    public Debt payDebt(String id, String userId, double payment) {
        Optional<Debt> debt = debtRepository.findById(id);
        debt.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deuda no encontrada"));

        if (userRepository.findById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        debt.get().setAccumulatedAmount(payment + debt.get().getAccumulatedAmount());
        debtRepository.save(debt.get());
        return debt.get();
    }
}
