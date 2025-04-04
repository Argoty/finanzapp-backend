package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.dtos.CategoryDTO;
import com.finanzapp.app_financiera.dtos.MontoPeriodoDTO;
import com.finanzapp.app_financiera.models.Record;
import com.finanzapp.app_financiera.services.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    @Autowired
    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    // Crea un nuevo record
    @PostMapping
    public ResponseEntity<Record> createRecord(@RequestBody Record record) {
        Record newRecord = recordService.agregarRecord(record);
        return new ResponseEntity<>(newRecord, HttpStatus.CREATED);
    }

    // Actualiza un record existente
    @PutMapping("/{id}")
    public ResponseEntity<Record> updateRecord(@PathVariable String id, @RequestBody Record record) {
        Record updatedRecord = recordService.update(id, record);
        return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
    }

    // Elimina un record por ID
    @DeleteMapping("/{id}/{userId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable String id, @PathVariable String userId) {
        recordService.deleteById(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Busca records aplicando filtro de búsqueda general y filtro de registros recientes (último período)\n    // Ejemplo de uso: /api/records/buscar?query=gasto&lastPeriod=12 semanas
    @GetMapping("/{userId}")
    public ResponseEntity<List<Record>> buscarRecords(
            @PathVariable String userId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String lastPeriod) {
        List<Record> records = recordService.buscarPorFiltros(userId, query, lastPeriod);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }
    
    @GetMapping("/categories/{userId}")
    public ResponseEntity<List<CategoryDTO>> getTotalesPorCategory(
            @PathVariable String userId,
            @RequestParam(required = false) String lastPeriod) {
        List<CategoryDTO> totales = recordService.obtenerTotalesPorCategory(userId,lastPeriod);
        return new ResponseEntity<>(totales, HttpStatus.OK);
    }
    
    @GetMapping("/buckets/{userId}")
    public ResponseEntity<List<MontoPeriodoDTO>> getBucketsPorPeriodo(
            @PathVariable String userId,
            @RequestParam String lastPeriod) {
        List<MontoPeriodoDTO> buckets = recordService.obtenerBucketsPorPeriodo(userId, lastPeriod);
        return new ResponseEntity<>(buckets, HttpStatus.OK);
    }
}

