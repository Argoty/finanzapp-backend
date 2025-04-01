package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.PlannedPayment;
import com.finanzapp.app_financiera.repository.PlannedPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class PlannedPaymentService {

    private final PlannedPaymentRepository pagoRepository;

    @Autowired
    public PlannedPaymentService(PlannedPaymentRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
        initSampleData();
    }

    // Inicialización de datos de ejemplo
    private void initSampleData() {
        save(new PlannedPayment(
                "Gasto",
                "Vivienda",
                "Pintada de paredes",
                LocalDate.of(2025, 4, 20),
                100000
        ));

        save(new PlannedPayment(
                "Gasto",
                "Transporte",
                "Pasaje de bus",
                LocalDate.of(2025, 4, 21),
                5000
        ));

        save(new PlannedPayment(
                "Ingreso",
                "Salario",
                "Pago mensual",
                LocalDate.of(2025, 4, 10),
                3500000
        ));
    }

    public PlannedPayment save(PlannedPayment pago) {
        return pagoRepository.save(pago);
    }

    public PlannedPayment agregarPago(PlannedPayment pago) {
        return save(pago);
    }

    public PlannedPayment findById(String id) {
        PlannedPayment pago = pagoRepository.findById(id);
        if (pago == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago con ID " + id + " no encontrado");
        }
        return pago;
    }

    public List<PlannedPayment> findAll() {
        return pagoRepository.findAll();
    }

    public List<PlannedPayment> buscarPorFiltros(String query) {
        return pagoRepository.buscarPorFiltros(query);
    }

    public PlannedPayment update(String id, PlannedPayment pago) {
        findById(id); // Valida que exista

        // Creamos un nuevo objeto con el mismo ID
        pago.setId(id);
        return pagoRepository.update(pago);
    }

    public void deleteById(String id) {
        findById(id);
        pagoRepository.deleteById(id);
    }
}
