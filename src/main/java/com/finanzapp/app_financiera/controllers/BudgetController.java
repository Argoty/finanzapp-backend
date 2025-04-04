package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.dtos.BudgetStatusResponse;
import com.finanzapp.app_financiera.models.Budget;
import com.finanzapp.app_financiera.services.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // Crea un nuevo presupuesto
    @PostMapping
    public ResponseEntity<Budget> createBudget(@RequestBody Budget budget) {
        Budget newBudget = budgetService.agregarPresupuesto(budget);
        return new ResponseEntity<>(newBudget, HttpStatus.CREATED);
    }

    // Actualiza un presupuesto existente
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable String id, @RequestBody Budget budget) {
        Budget updatedBudget = budgetService.update(id, budget);
        return new ResponseEntity<>(updatedBudget, HttpStatus.OK);
    }

    // Elimina un presupuesto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable String id) {
        budgetService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Busca presupuestos usando filtros de búsqueda y período
    @GetMapping("/buscar")
    public ResponseEntity<List<Budget>> buscarBudgets(
            @PathVariable String userId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String period) {
        List<Budget> budgets = budgetService.buscarPorFiltros(userId,query, period);
        return new ResponseEntity<>(budgets, HttpStatus.OK);
    }
    @GetMapping("/status/{userId}")
    public ResponseEntity<List<BudgetStatusResponse>> getAllBudgetsStatus(@PathVariable String userId) {
        List<BudgetStatusResponse> statusList = budgetService.getAllBudgetsStatus(userId);
        return ResponseEntity.ok(statusList);
    }
}

