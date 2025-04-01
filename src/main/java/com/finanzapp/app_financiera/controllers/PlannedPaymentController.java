package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.models.PlannedPayment;
import com.finanzapp.app_financiera.services.PlannedPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planned-payments")
public class PlannedPaymentController {

    private final PlannedPaymentService pagoService;

    @Autowired
    public PlannedPaymentController(PlannedPaymentService pagoService) {
        this.pagoService = pagoService;
    }

    // Crea un nuevo pago planeado
    @PostMapping
    public ResponseEntity<PlannedPayment> createPago(@RequestBody PlannedPayment pago) {
        PlannedPayment newPago = pagoService.agregarPago(pago);
        return new ResponseEntity<>(newPago, HttpStatus.CREATED);
    }

    // Actualiza un pago planeado existente
    @PutMapping("/{id}")
    public ResponseEntity<PlannedPayment> updatePago(@PathVariable String id, @RequestBody PlannedPayment pago) {
        PlannedPayment updatedPago = pagoService.update(id, pago);
        return new ResponseEntity<>(updatedPago, HttpStatus.OK);
    }

    // Elimina un pago planeado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePago(@PathVariable String id) {
        pagoService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Busca pagos planeados usando filtros de búsqueda y período reciente
    @GetMapping
    public ResponseEntity<List<PlannedPayment>> buscarPagos(
            @RequestParam(required = false) String query) {
        List<PlannedPayment> pagos = pagoService.buscarPorFiltros(query);
        return new ResponseEntity<>(pagos, HttpStatus.OK);
    }
}

