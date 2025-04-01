package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.models.Saving;
import com.finanzapp.app_financiera.services.SavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ahorros")
public class SavingController {

    private final SavingService savingService;

    @Autowired
    public SavingController(SavingService savingService) {
        this.savingService = savingService;
    }

    @GetMapping("")
    public ResponseEntity<List<Saving>> findAllSavings() {
        return ResponseEntity.ok(savingService.findAllSavings());
    }

    @PostMapping("")
    public ResponseEntity<Saving> addSaving(@RequestBody Saving saving) {
        return ResponseEntity.ok(savingService.save(saving));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Saving> deleteSaving(@PathVariable String id) {
        return ResponseEntity.ok(savingService.deleteSavingById(id));
    }

}
