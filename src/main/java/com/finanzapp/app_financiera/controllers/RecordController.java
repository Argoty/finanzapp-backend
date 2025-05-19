package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.dtos.CategoryDTO;
import com.finanzapp.app_financiera.dtos.MontoPeriodoDTO;
import com.finanzapp.app_financiera.models.Record;
import com.finanzapp.app_financiera.services.RecordService;
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
@RequestMapping("/api/records")
@Tag(name = "Records", description = "API para la gestión de registros financieros")
public class RecordController {

    private final RecordService recordService;

    @Autowired
    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    // Crea un nuevo record
    @PostMapping
    @Operation(summary = "Crear un nuevo registro", description = "Crea un nuevo registro financiero.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @ApiResponse(responseCode = "401", description = "Token no proporcionado o inválido"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso"),
            @ApiResponse(responseCode = "409", description = "No concuerda body con id de usuario del token")
    })
    public ResponseEntity<Record> createRecord(@RequestBody @Parameter(description = "Datos del registro a crear") Record record,
            @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token) {
        Record newRecord = recordService.save(record, token);
        return new ResponseEntity<>(newRecord, HttpStatus.CREATED);
    }

    // Actualiza un record existente
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un registro existente", description = "Actualiza la información de un registro financiero existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado con éxito"),
            @ApiResponse(responseCode = "404", description = "Registro o usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @ApiResponse(responseCode = "401", description = "Token no proporcionado o inválido"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso"),
            @ApiResponse(responseCode = "409", description = "No concuerda body con id de usuario del token")
    })
    public ResponseEntity<Record> updateRecord(
            @PathVariable @Parameter(description = "ID del registro a actualizar") int id,
            @RequestBody @Parameter(description = "Nuevos datos del registro") Record record,
            @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token) {
        Record updatedRecord = recordService.update(id, record, token);
        return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
    }

    // Elimina un record por ID
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un registro por ID", description = "Elimina un registro financiero específico basado en su ID y el ID del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado o el usuario no es el propietario"),
            @ApiResponse(responseCode = "401", description = "Token no proporcionado o inválido"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso"),
            @ApiResponse(responseCode = "409", description = "No concuerda body con id de usuario del token")
    })
    public ResponseEntity<Void> deleteRecord(
            @PathVariable @Parameter(description = "ID del registro a eliminar") int id,
            @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token) {
        recordService.deleteById(id, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Busca records aplicando filtro de búsqueda general y filtro de registros recientes (último período)
    @GetMapping
    @Operation(summary = "Buscar registros por usuario y filtros", description = "Busca registros financieros para un usuario específico, permitiendo filtrar por una consulta general y un período de tiempo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de registros encontrados"),
            @ApiResponse(responseCode = "401", description = "Token no proporcionado o inválido"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso"),
    })
    public ResponseEntity<List<Record>> buscarRecords(
            @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token,
            @RequestParam(required = false) @Parameter(description = "Término de búsqueda general (opcional)") String query,
            @RequestParam(required = false) @Parameter(description = "Filtro de período reciente (ej: '1 semana', '1 mes', '3 meses', '6 meses', '1 año') (opcional)") String lastPeriod) {
        List<Record> records = recordService.buscarPorFiltros(token, query, lastPeriod);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    @GetMapping("/categories")
    @Operation(summary = "Obtener totales de gastos por categoría", description = "Obtiene los totales de gastos agrupados por categoría para un usuario específico, con opción de filtrar por período.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de totales por categoría"),
            @ApiResponse(responseCode = "401", description = "Token no proporcionado o inválido"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso"),
    })
    public ResponseEntity<List<CategoryDTO>> getTotalesPorCategory(
            @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token,
            @RequestParam(required = false) @Parameter(description = "Filtro de período (ej: '1 semana', '1 mes', '3 meses', '6 meses', '1 año') (opcional)") String lastPeriod) {
        List<CategoryDTO> totales = recordService.obtenerTotalesPorCategory(token, lastPeriod);
        return new ResponseEntity<>(totales, HttpStatus.OK);
    }

    @GetMapping("/buckets")
    @Operation(summary = "Obtener buckets de ingresos y gastos por período", description = "Obtiene los montos de ingresos y gastos agrupados en buckets según un período especificado para un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de buckets por período"),
            @ApiResponse(responseCode = "400", description = "Formato de período no válido"),
            @ApiResponse(responseCode = "401", description = "Token no proporcionado o inválido"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso"),
    })
    public ResponseEntity<List<MontoPeriodoDTO>> getBucketsPorPeriodo(
            @RequestHeader("Authorization") @Parameter(description = "Token de sesion", required = true) String token,
            @RequestParam @Parameter(description = "Período para agrupar los buckets (ej: '1 semana', '1 mes', '3 meses', '6 meses', '1 año')") String lastPeriod) {
        List<MontoPeriodoDTO> buckets = recordService.obtenerBucketsPorPeriodo(token, lastPeriod);
        return new ResponseEntity<>(buckets, HttpStatus.OK);
    }
}


