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
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos")
    })
    public ResponseEntity<Record> createRecord(@RequestBody @Parameter(description = "Datos del registro a crear") Record record) {
        Record newRecord = recordService.save(record);
        return new ResponseEntity<>(newRecord, HttpStatus.CREATED);
    }

    // Actualiza un record existente
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un registro existente", description = "Actualiza la información de un registro financiero existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado con éxito"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos")
    })
    public ResponseEntity<Record> updateRecord(
            @PathVariable @Parameter(description = "ID del registro a actualizar") int id,
            @RequestBody @Parameter(description = "Nuevos datos del registro") Record record) {
        Record updatedRecord = recordService.update(id, record);
        return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
    }

    // Elimina un record por ID
    @DeleteMapping("/{id}/{userId}")
    @Operation(summary = "Eliminar un registro por ID", description = "Elimina un registro financiero específico basado en su ID y el ID del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado o el usuario no es el propietario")
    })
    public ResponseEntity<Void> deleteRecord(
            @PathVariable @Parameter(description = "ID del registro a eliminar") int id,
            @PathVariable @Parameter(description = "ID del usuario que creó el registro") int userId) {
        recordService.deleteById(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Busca records aplicando filtro de búsqueda general y filtro de registros recientes (último período)
    @GetMapping("/{userId}")
    @Operation(summary = "Buscar registros por usuario y filtros", description = "Busca registros financieros para un usuario específico, permitiendo filtrar por una consulta general y un período de tiempo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de registros encontrados")
    })
    public ResponseEntity<List<Record>> buscarRecords(
            @PathVariable @Parameter(description = "ID del usuario para buscar registros") int userId,
            @RequestParam(required = false) @Parameter(description = "Término de búsqueda general (opcional)") String query,
            @RequestParam(required = false) @Parameter(description = "Filtro de período reciente (ej: '1 semana', '1 mes', '3 meses', '6 meses', '1 año') (opcional)") String lastPeriod) {
        List<Record> records = recordService.buscarPorFiltros(userId, query, lastPeriod);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    @GetMapping("/categories/{userId}")
    @Operation(summary = "Obtener totales de gastos por categoría", description = "Obtiene los totales de gastos agrupados por categoría para un usuario específico, con opción de filtrar por período.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de totales por categoría")
    })
    public ResponseEntity<List<CategoryDTO>> getTotalesPorCategory(
            @PathVariable @Parameter(description = "ID del usuario para obtener los totales") int userId,
            @RequestParam(required = false) @Parameter(description = "Filtro de período (ej: '1 semana', '1 mes', '3 meses', '6 meses', '1 año') (opcional)") String lastPeriod) {
        List<CategoryDTO> totales = recordService.obtenerTotalesPorCategory(userId, lastPeriod);
        return new ResponseEntity<>(totales, HttpStatus.OK);
    }

    @GetMapping("/buckets/{userId}")
    @Operation(summary = "Obtener buckets de ingresos y gastos por período", description = "Obtiene los montos de ingresos y gastos agrupados en buckets según un período especificado para un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de buckets por período"),
            @ApiResponse(responseCode = "400", description = "Formato de período no válido")
    })
    public ResponseEntity<List<MontoPeriodoDTO>> getBucketsPorPeriodo(
            @PathVariable @Parameter(description = "ID del usuario para obtener los buckets") int userId,
            @RequestParam @Parameter(description = "Período para agrupar los buckets (ej: '1 semana', '1 mes', '3 meses', '6 meses', '1 año')") String lastPeriod) {
        List<MontoPeriodoDTO> buckets = recordService.obtenerBucketsPorPeriodo(userId, lastPeriod);
        return new ResponseEntity<>(buckets, HttpStatus.OK);
    }
}


