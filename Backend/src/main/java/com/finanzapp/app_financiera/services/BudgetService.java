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

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

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

    // Inicialización de datos de ejemplo
    private void initSampleData() {
        save(new Budget("Alimentación", "Comida y restaurantes", "Mensual", 200000));
        save(new Budget("Transporte", "Movilidad diaria", "Semanal", 25000));
        save(new Budget("Tecnología", "Accesorios y dispositivos", "Trimestral", 500000));
        save(new Budget("Deportes y Fitness", "Entrenamiento y gimnasio", "Mensual", 150000));
    }

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget agregarPresupuesto(Budget budget) {
        return save(budget);
    }

    public Budget findById(String id) {
        Budget budget = budgetRepository.findById(id);
        if (budget == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Presupuesto con ID " + id + " no encontrado");
        }
        return budget;
    }

    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }

    public List<Budget> buscarPorFiltros(String query, String period) {
        return budgetRepository.buscarPorFiltros(query, period);
    }

    public Budget update(String id, Budget budget) {
        findById(id); // Valida que exista
        budget.setId(id);
        return budgetRepository.update(budget);
    }

    public void deleteById(String id) {
        findById(id);
        budgetRepository.deleteById(id);
    }

    public List<BudgetStatusResponse> getAllBudgetsStatus() {
    List<Budget> budgets = budgetRepository.findAll();
        LocalDate today = LocalDate.now();
    
    return budgets.stream().map(budget -> {
        LocalDate startDate = calculateStartDate(today, budget.getPeriod());

        List<Record> expenseRecords = recordRepository.findAll().stream()
                .filter(record -> record.getCategory().equalsIgnoreCase(budget.getCategory()))
                .filter(record -> "Gasto".equalsIgnoreCase(record.getType()))
                .filter(record -> !record.getDate().toLocalDate().isBefore(startDate))
                .collect(Collectors.toList());

        int totalSpent = expenseRecords.stream().mapToInt(Record::getAmount).sum();

        return new BudgetStatusResponse(budget, totalSpent);
    }).collect(Collectors.toList());
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

