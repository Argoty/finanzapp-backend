package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.Debt;
import com.finanzapp.app_financiera.models.Saving;
import com.finanzapp.app_financiera.models.User;
import com.finanzapp.app_financiera.repository.DebtRepository;
import com.finanzapp.app_financiera.repository.UserRepository;
import java.time.LocalDate;

import com.finanzapp.app_financiera.security.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @Autowired
    public DebtService(DebtRepository debtRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
        initDebts();
        this.jwtUtil = jwtUtil;
    }

    public void initDebts() {
        /*save(new Debt(1, "Casa", 0, 10, LocalDate.of(2025, 6, 2)));
        save(new Debt(1, "Auto", 500, 5000, LocalDate.now().plusDays(7)));
        save(new Debt(1, "Crédito personal", 200, 2000, LocalDate.now().plusDays(20)));
        save(new Debt(1, "Estudios", 0, 10000, LocalDate.now().plusDays(29)));
        save(new Debt(1, "Vacaciones", 300, 3000, LocalDate.now().plusDays(14)));
        save(new Debt(1, "Muebles", 150, 1500, LocalDate.now().plusDays(2)));
        save(new Debt(1, "Electrodomésticos", 100, 1200, LocalDate.now().plusDays(12)));
        save(new Debt(1, "Médico", 0, 2500, LocalDate.now().plusDays(5)));
        save(new Debt(1, "Reparaciones", 400, 4000, LocalDate.now().plusDays(27)));
        save(new Debt(1, "Negocio", 1000, 15000, LocalDate.now().minusDays(10)));*/
    }

    public List<Debt> findAllDebts(String token) {
        String email = jwtUtil.extractEmailFromAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return debtRepository.findAllByUserId(user.getId());
    }

    public Debt save(Debt debt, String token) {
        String email = jwtUtil.extractEmailFromAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if(!(user.getId() == debt.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token no valido");
        }
        return debtRepository.save(debt);
    }

    public Debt deleteDebtById(int id, String token) {
        Optional<Debt> debt = debtRepository.findById(id);
        debt.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deuda no encontrada"));

        String email = jwtUtil.extractEmailFromAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if(!(user.getId() == debt.get().getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token no valido");
        }

        debtRepository.deleteById(id);
        return debt.get();
    }

    public Debt updateDebt(Debt debt, String token, int id) {
        debtRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deuda no encontrada"));

        String email = jwtUtil.extractEmailFromAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if(!(user.getId() == debt.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token no valido");
        }

        debt.setId(id);
        return debtRepository.save(debt);
    }

    public Debt payDebt(int id, int userId, double payment, String token) {
        Optional<Debt> debt = debtRepository.findById(id);
        debt.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deuda no encontrada"));

        String email = jwtUtil.extractEmailFromAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if(!(user.getId() == debt.get().getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token no valido");
        }

        debt.get().setAccumulatedAmount(payment + debt.get().getAccumulatedAmount());
        debtRepository.save(debt.get());
        return debt.get();
    }
}
