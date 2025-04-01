package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.models.Debt;
import com.finanzapp.app_financiera.repository.DebtRepository;
import com.finanzapp.app_financiera.services.DebtService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/deudas")
public class DebtController {

    private DebtService debtService;

    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

    @GetMapping("")
    public List<Debt> findAllDebts() {
        return debtService.findAllDebts();
    }

    @PostMapping("")
    public Debt addDebt(@RequestBody Debt doubt) {
        return debtService.save(doubt);
    }

    @DeleteMapping("/{id}")
    public Debt deleteDebt(@PathVariable String id) {
        return debtService.deleteDebtById(id);
    }
}
