package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.models.Debt;
import com.finanzapp.app_financiera.models.Saving;
import com.finanzapp.app_financiera.services.DebtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/deudas")
@Tag(name = "Debts", description = "API para la gestión de deudas")
public class DebtController {

    private final DebtService debtService;

    @Autowired
    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

    @Operation(summary = "Actualizar una deuda existente de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deuda actualizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Deuda no encontrada")
    })
    @PutMapping("/{userId}/{id}")
    public ResponseEntity<Debt> updateDebt(
            @Parameter(description = "Cuerpo de la deuda") @RequestBody Debt debt,
            @Parameter(description = "ID del usuario", required = true) @PathVariable int userId,
            @Parameter(description = "ID de la deuda", required = true) @PathVariable int id) {

        return ResponseEntity.ok(debtService.updateDebt(debt, userId, id));
    }

    @Operation(summary = "Obtener todas las deudas de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de deudas obtenida correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<Debt>> findAllDebts(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable int userId) {
        return ResponseEntity.ok(debtService.findAllDebts(userId));
    }

    @Operation(summary = "Agregar una nueva deuda")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deuda agregada correctamente")
    })
    @PostMapping("")
    public ResponseEntity<Debt> addDebt(
            @Parameter(description = "Objeto deuda que se desea agregar", required = true)
            @RequestBody Debt doubt) {
        return ResponseEntity.ok(debtService.save(doubt));
    }

    @Operation(summary = "Eliminar una deuda por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deuda eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Deuda no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Debt> deleteDebt(
            @Parameter(description = "ID de la deuda a eliminar", required = true)
            @PathVariable int id) {
        return ResponseEntity.ok(debtService.deleteDebtById(id));
    }

    @Operation(summary = "Abonar a una deuda existente de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Abono realizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Monto inválido o datos incorrectos"),
        @ApiResponse(responseCode = "404", description = "No se encontró la deuda o el usuario")
    })
    @PatchMapping("/{userId}/{debtId}/abonar")
    public ResponseEntity<Debt> abonarDeuda(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable int userId,
            @Parameter(description = "ID de la deuda", required = true)
            @PathVariable int debtId,
            @Parameter(description = "Monto a abonar", required = true, example = "150.0")
            @RequestParam("amount") double amount) {
        return ResponseEntity.ok(debtService.payDebt(debtId, userId, amount));
    }

}
