package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.dtos.BudgetStatusResponse;
import com.finanzapp.app_financiera.models.Budget;
import com.finanzapp.app_financiera.services.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@Tag(name = "Budgets", description = "API para la gestión de presupuestos")
public class BudgetController {

    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // Crea un nuevo presupuesto
    @PostMapping
    @Operation(summary = "Crear un nuevo presupuesto", description = "Crea un nuevo presupuesto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Presupuesto creado con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos de presupuesto inválidos"),
        @ApiResponse(responseCode = "401", description = "Token no proporcionado o inválido"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso"),
        @ApiResponse(responseCode = "409", description = "No concuerda body con id de usuario del token")
    })
    public ResponseEntity<Budget> createBudget(@RequestBody @Parameter(description = "Datos del presupuesto a crear") Budget budget,
            @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token) {
        Budget newBudget = budgetService.save(budget, token);
        return new ResponseEntity<>(newBudget, HttpStatus.CREATED);
    }

    // Actualiza un presupuesto existente
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un presupuesto existente", description = "Actualiza la información de un presupuesto existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Presupuesto actualizado con éxito"),
        @ApiResponse(responseCode = "404", description = "Presupuesto no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de presupuesto inválidos"),
        @ApiResponse(responseCode = "401", description = "Token no proporcionado o inválido"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso"),
        @ApiResponse(responseCode = "409", description = "No concuerda body con id de usuario del token")
    })
    public ResponseEntity<Budget> updateBudget(@PathVariable @Parameter(description = "ID del presupuesto a actualizar") int id,
            @RequestBody @Parameter(description = "Nuevos datos del presupuesto") Budget budget,
            @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token) {
        Budget updatedBudget = budgetService.update(id, budget, token);
        return new ResponseEntity<>(updatedBudget, HttpStatus.OK);
    }

    // Elimina un presupuesto
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un presupuesto por ID", description = "Elimina un presupuesto específico basado en su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Presupuesto eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "Presupuesto no encontrado"),
        @ApiResponse(responseCode = "401", description = "Token no proporcionado o inválido"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso"),
        @ApiResponse(responseCode = "409", description = "No concuerda body con id de usuario del token")
    })
    public ResponseEntity<Void> deleteBudget(@PathVariable @Parameter(description = "ID del presupuesto a eliminar") int id,
            @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token) {
        budgetService.deleteById(id, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/status")
    @Operation(summary = "Obtener el estado de todos los presupuestos de un usuario", description = "Obtiene el estado actual de todos los presupuestos para un usuario específico, con opción de filtrar por período.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista del estado de los presupuestos"),
        @ApiResponse(responseCode = "401", description = "Token no proporcionado o inválido"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso"),
    })
    public ResponseEntity<List<BudgetStatusResponse>> getAllBudgetsStatus(
            @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token,
            @RequestParam(required = false) @Parameter(description = "Período para filtrar los presupuestos (ej: 'semanal', 'mensual', 'trimestral', 'semestral', 'anual') (opcional)") String period) {
        List<BudgetStatusResponse> statusList = budgetService.getAllBudgetsStatus(token, period);
        return ResponseEntity.ok(statusList);
    }
}
