package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.Budget;
import com.finanzapp.app_financiera.dtos.BudgetStatusResponse;
import com.finanzapp.app_financiera.models.Record;
import com.finanzapp.app_financiera.repository.BudgetRepository;
import com.finanzapp.app_financiera.repository.RecordRepository;
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

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, RecordRepository recordRepository) {
        this.budgetRepository = budgetRepository;
        this.recordRepository = recordRepository;
        initSampleData();
    }

    private void initSampleData() {
//        save(new Budget(1, "Alimentación", "Comida y restaurantes", "Mensual", 200000));
//        save(new Budget(1, "Transporte", "Movilidad diaria", "Semanal", 25000));
//        save(new Budget(1, "Tecnología", "Accesorios y dispositivos", "Trimestral", 500000));
//        save(new Budget(1, "Deportes y Fitness", "Entrenamiento y gimnasio", "Mensual", 150000));
    }

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget agregarPresupuesto(Budget budget) {
        return save(budget);
    }

    public Budget findById(int id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Presupuesto con ID " + id + " no encontrado"));
    }

    public Budget update(int id, Budget budget) {
        findById(id);

        budget.setId(id);
        return budgetRepository.save(budget);
    }

    public void deleteById(int id) {
        Budget budget = findById(id);
        budgetRepository.delete(budget);
    }

    public List<BudgetStatusResponse> getAllBudgetsStatus(int userId, String period) {
        List<Budget> budgets = (period == null)
                ? budgetRepository.findAllByUserId(userId)
                : budgetRepository.findAllByUserIdAndPeriod(userId, period);

        return budgets.stream()
                .map(budget -> {
                    LocalDate startDate = calculateStartDate(LocalDate.now(), budget.getPeriod());

                    List<Record> expenseRecords = recordRepository
                            .findAllByUserIdAndTypeAndCategoryAndDateGreaterThanEqual(
                                    userId,
                                    "Gasto",
                                    budget.getCategory(),
                                    startDate.atStartOfDay()
                            );

                    int totalSpent = expenseRecords.stream()
                            .mapToInt(Record::getAmount)
                            .sum();

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
