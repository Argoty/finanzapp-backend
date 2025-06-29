package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.models.Saving;
import com.finanzapp.app_financiera.services.SavingService;
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
@RequestMapping("/api/ahorros")
@Tag(name = "Savings", description = "API para la gestión de ahorros")
public class SavingController {

    private final SavingService savingService;

    @Autowired
    public SavingController(SavingService savingService) {
        this.savingService = savingService;
    }


    @Operation(summary = "Actualizar un ahorro existente de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ahorro actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Ahorro no encontrado"),
            @ApiResponse(responseCode = "403", description = "TOKEN NO VALIDO")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Saving> updateSaving(
            @Parameter(description = "Cuerpo del ahorro") @RequestBody Saving saving,
            @Parameter(description = "ID del ahorro", required = true) @PathVariable int id,
            @Parameter(description = "Token de sesion", required = true)
            @RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(savingService.updateSaving(saving, token, id));
    }


    @Operation(summary = "Obtener todos los ahorros de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de ahorros obtenida correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontraron ahorros para este usuario"),
            @ApiResponse(responseCode = "403", description = "TOKEN NO VALIDO")
    })
    @GetMapping("")
    public ResponseEntity<List<Saving>> findAllSavings(
            @Parameter(description = "Token de sesion", required = true)
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(savingService.findAllSavings(token));
    }


    @Operation(summary = "Agregar un nuevo ahorro")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ahorro agregado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (ID de usuario, título o montos incorrectos)"),
            @ApiResponse(responseCode = "403", description = "TOKEN NO VALIDO")
    })
    @PostMapping("")
    public ResponseEntity<Saving> addSaving(
            @Parameter(description = "Objeto ahorro que se desea agregar", required = true)
            @RequestBody Saving saving,
            @Parameter(description = "Token de sesion", required = true)
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(savingService.save(saving, token));
    }


    @Operation(summary = "Eliminar un ahorro por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ahorro eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Ahorro no encontrado"),
            @ApiResponse(responseCode = "403", description = "TOKEN NO VALIDO")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Saving> deleteSaving(
            @Parameter(description = "ID del ahorro a eliminar", required = true)
            @PathVariable int id,
            @Parameter(description = "Token de sesion", required = true)
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(savingService.deleteSavingById(id, token));
    }


    @Operation(summary = "Agregar dinero a un ahorro existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dinero ahorrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Monto inválido o datos incorrectos"),
        @ApiResponse(responseCode = "404", description = "No se encontró el ahorro o el usuario"),
            @ApiResponse(responseCode = "403", description = "TOKEN NO VALIDO")
    })
    @PatchMapping("/{savingId}/ahorrar")
    public ResponseEntity<Saving> ahorrar(
            @Parameter(description = "ID del ahorro", required = true)
            @PathVariable int savingId,
            @Parameter(description = "Monto a ahorrar", required = true, example = "200.0")
            @RequestParam("amount") double amount,
            @Parameter(description = "Token de sesion", required = true)
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(savingService.saveToSaving(savingId, token, amount));
    }

}
