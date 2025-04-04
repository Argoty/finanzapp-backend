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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable String id) {
        recordService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Busca records aplicando filtro de búsqueda general y filtro de registros recientes (último período)\n    // Ejemplo de uso: /api/records/buscar?query=gasto&lastPeriod=12 semanas
    @GetMapping
    public ResponseEntity<List<Record>> buscarRecords(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String lastPeriod) {
        List<Record> records = recordService.buscarPorFiltros(query, lastPeriod);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getTotalesPorCategory(
            @RequestParam(required = false) String lastPeriod) {
        List<CategoryDTO> totales = recordService.obtenerTotalesPorCategory(lastPeriod);
        return new ResponseEntity<>(totales, HttpStatus.OK);
    }
    
    @GetMapping("/buckets")
    public ResponseEntity<List<MontoPeriodoDTO>> getBucketsPorPeriodo(
            @RequestParam String lastPeriod) {
        List<MontoPeriodoDTO> buckets = recordService.obtenerBucketsPorPeriodo(lastPeriod);
        return new ResponseEntity<>(buckets, HttpStatus.OK);
    }
}

