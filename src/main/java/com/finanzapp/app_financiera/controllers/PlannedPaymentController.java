package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.models.PlannedPayment;
import com.finanzapp.app_financiera.services.PlannedPaymentService;
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
@RequestMapping("/api/planned-payments")
@Tag(name = "Planned Payments", description = "API para la gestión de pagos planeados")
public class PlannedPaymentController {

    private final PlannedPaymentService pagoService;

    @Autowired
    public PlannedPaymentController(PlannedPaymentService pagoService) {
        this.pagoService = pagoService;
    }

    // Crea un nuevo pago planeado
    @PostMapping
    @Operation(summary = "Crear un nuevo pago planeado", description = "Crea un nuevo registro de pago planeado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pago planeado creado con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos del pago planeado inválidos")
    })
    public ResponseEntity<PlannedPayment> createPago(
            @RequestBody @Parameter(description = "Datos del pago planeado a crear") PlannedPayment pago,
            @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token) {
        PlannedPayment newPago = pagoService.save(pago, token);
        return new ResponseEntity<>(newPago, HttpStatus.CREATED);
    }

    // Actualiza un pago planeado existente
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un pago planeado existente", description = "Actualiza la información de un pago planeado específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago planeado actualizado con éxito"),
        @ApiResponse(responseCode = "404", description = "Pago planeado no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos del pago planeado inválidos")
    })
    public ResponseEntity<PlannedPayment> updatePago(
            @PathVariable @Parameter(description = "ID del pago planeado a actualizar") int id, 
            @RequestBody @Parameter(description = "Nuevos datos del pago planeado") PlannedPayment pago,
            @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token) {
        PlannedPayment updatedPago = pagoService.update(id, pago, token);
        return new ResponseEntity<>(updatedPago, HttpStatus.OK);
    }

    // Elimina un pago planeado
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un pago planeado por ID", description = "Elimina un pago planeado específico basado en su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pago planeado eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "Pago planeado no encontrado")
    })
    public ResponseEntity<Void> deletePago(@PathVariable @Parameter(description = "ID del pago planeado a eliminar") int id, @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token) {
        pagoService.deleteById(id, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Busca pagos planeados usando filtros de búsqueda y período reciente
    @GetMapping
    @Operation(summary = "Buscar pagos planeados por usuario y filtros", description = "Busca pagos planeados para un usuario específico, con opciones de filtrado por texto y período futuro.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pagos planeados encontrados")
    })
    public ResponseEntity<List<PlannedPayment>> buscarPagos(
            @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token,
            @RequestParam(required = false) @Parameter(description = "Texto para filtrar los pagos planeados por tipo, categoría, nombre, fecha de vencimiento o monto (opcional)") String query,
            @RequestParam(required = false) @Parameter(description = "Período futuro para filtrar los pagos planeados (ej: '1 semana', '1 mes', '3 meses', '6 meses', '1 año') (opcional)") String futurePeriod) {
        List<PlannedPayment> pagos = pagoService.buscarPorFiltros(token, query, futurePeriod);
        return new ResponseEntity<>(pagos, HttpStatus.OK);
    }

    // Actualiza el estado del pago y agrega fecha de realizacion del pago
    @PutMapping("/confirm/{id}")
    @Operation(summary = "Confirmar un pago planeado", description = "Marca un pago planeado como realizado, registrando la fecha de realización.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago planeado confirmado con éxito"),
        @ApiResponse(responseCode = "404", description = "Pago planeado no encontrado"),
        @ApiResponse(responseCode = "409", description = "El pago planeado ya ha sido confirmado")
    })
    public ResponseEntity<PlannedPayment> confirmarPago(@PathVariable @Parameter(description = "ID del pago planeado a confirmar") int id, @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token) {
        PlannedPayment pagoConfirmado = pagoService.confirmarPago(id, token);
        return new ResponseEntity<>(pagoConfirmado, HttpStatus.OK);
    }
}
