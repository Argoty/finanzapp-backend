package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.models.Debt;
import com.finanzapp.app_financiera.repository.DebtRepository;
import com.finanzapp.app_financiera.services.DebtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/deudas")
public class DebtController {

    private DebtService debtService;

    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

    @GetMapping("/userId")
    public ResponseEntity<List<Debt>> findAllDebts(@PathVariable String userId) {
        return ResponseEntity.ok(debtService.findAllDebts());
    }

    @PostMapping("")
    public ResponseEntity<Debt> addDebt(@RequestBody Debt doubt) {
        return ResponseEntity.ok(debtService.save(doubt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Debt> deleteDebt(@PathVariable String id) {
        return ResponseEntity.ok(debtService.deleteDebtById(id));
    }
}
