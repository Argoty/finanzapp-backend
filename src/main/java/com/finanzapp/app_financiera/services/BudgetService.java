package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.Budget;
import com.finanzapp.app_financiera.models.User;

import com.finanzapp.app_financiera.dtos.BudgetStatusResponse;
import com.finanzapp.app_financiera.models.Record;
import com.finanzapp.app_financiera.repository.BudgetRepository;
import com.finanzapp.app_financiera.repository.RecordRepository;
import com.finanzapp.app_financiera.repository.UserRepository;
import com.finanzapp.app_financiera.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, RecordRepository recordRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.budgetRepository = budgetRepository;
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        //initSampleData();
    }

    private void initSampleData() {
//        save(new Budget(1, "Alimentación", "Comida y restaurantes", "Mensual", 200000));
//        save(new Budget(1, "Transporte", "Movilidad diaria", "Semanal", 25000));
//        save(new Budget(1, "Tecnología", "Accesorios y dispositivos", "Trimestral", 500000));
//        save(new Budget(1, "Deportes y Fitness", "Entrenamiento y gimnasio", "Mensual", 150000));
    }

    private int getUserIdWithToken(String token) {
        String email = jwtUtil.extractEmailFromAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return user.getId();
    }

    private void validateTokenWithEntity(Budget budget, String token) {
        int userId = getUserIdWithToken(token);
        if (!(userId == budget.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token no valido");
        }
    }

    public Budget save(Budget budget, String token) {
        validateTokenWithEntity(budget, token);
        return budgetRepository.save(budget);
    }

    public Budget findById(int id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Presupuesto con ID " + id + " no encontrado"));
    }

    public Budget update(int id, Budget budget, String token) {
        validateTokenWithEntity(budget, token);
        findById(id);
        budget.setId(id);
        return budgetRepository.save(budget);
    }

    public void deleteById(int id, String token) {
        Budget budget = findById(id);
        validateTokenWithEntity(budget, token);
        budgetRepository.delete(budget);
    }

    public List<BudgetStatusResponse> getAllBudgetsStatus(String token, String period) {
        int userId = getUserIdWithToken(token);
        // Obtiene todos los presupuestos del usuario, filtrando por periodo si se indica
        List<Budget> budgets = (period == null)
                ? budgetRepository.findAllByUserId(userId)
                : budgetRepository.findAllByUserIdAndPeriod(userId, period);

        // Procesa cada presupuesto para calcular su estado
        return budgets.stream()
                .map(budget -> {
                    // Calcula la fecha de inicio del presupuesto basado en su periodo
                    LocalDate startDate = calculateStartDate(LocalDate.now(), budget.getPeriod());

                    // Obtiene los registros de gastos para la categoría desde la fecha de inicio
                    List<Record> expenseRecords = recordRepository
                            .findAllByUserIdAndTypeAndCategoryAndDateGreaterThanEqual(
                                    userId,
                                    "Gasto",
                                    budget.getCategory(),
                                    startDate.atStartOfDay()
                            );

                    // Suma el total gastado en el periodo
                    int totalSpent = expenseRecords.stream()
                            .mapToInt(Record::getAmount)
                            .sum();

                    // Retorna el estado del presupuesto con los datos calculados
                    return new BudgetStatusResponse(budget, totalSpent, startDate);
                })
                .collect(Collectors.toList());
    }

    private LocalDate calculateStartDate(LocalDate today, String period) {
        switch (period.toLowerCase()) {
            case "semanal":
                return today.with(java.time.DayOfWeek.MONDAY);
            case "mensual":
                return today.withDayOfMonth(1);
            case "trimestral":
                return today.withMonth(((today.getMonthValue() - 1) / 3) * 3 + 1).withDayOfMonth(1);
            case "semestral":
                return today.withMonth((today.getMonthValue() <= 6) ? 1 : 7).withDayOfMonth(1);
            case "anual":
                return today.with(TemporalAdjusters.firstDayOfYear());
            default:
                throw new IllegalArgumentException("Período no válido: " + period);
        }
    }
}
