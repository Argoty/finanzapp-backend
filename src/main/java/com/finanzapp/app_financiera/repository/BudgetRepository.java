package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.Budget;
import org.springframework.stereotype.Repository;
import java.util.stream.Collectors;

import java.util.*;

@Repository
public class BudgetRepository {

    private final Map<String, Budget> tablaPresupuestos = new HashMap<>();

    public Budget save(Budget budget) {
        tablaPresupuestos.put(budget.getId(), budget);
        return budget;
    }

    public Budget findById(String id) {
        return tablaPresupuestos.get(id);
    }

    public List<Budget> findAll(String userId) {
        return tablaPresupuestos.values()
                .stream()
                .filter(e -> e.getUserId().equals(userId))
                .collect(Collectors.toList()); // Convertimos el Stream en una lista
    }

    public void deleteById(String id) {
        tablaPresupuestos.remove(id);
    }

    public Budget update(Budget budget) {
        if (tablaPresupuestos.containsKey(budget.getId())) {
            tablaPresupuestos.put(budget.getId(), budget);
            return budget;
        }
        return null;
    }

    public List<Budget> buscarPorFiltros(String userId, String query, String period) {
        return tablaPresupuestos.values().stream()
                .filter(b -> b.getUserId().equals(userId))
                .filter(b -> (query == null || query.isEmpty())
                || b.getCategory().toLowerCase().contains(query.toLowerCase())
                || b.getName().toLowerCase().contains(query.toLowerCase())
                || String.valueOf(b.getLimitAmount()).contains(query))
                .filter(b -> (period == null || period.isEmpty()) || b.getPeriod().equalsIgnoreCase(period))
                .collect(Collectors.toList());
    }

}
