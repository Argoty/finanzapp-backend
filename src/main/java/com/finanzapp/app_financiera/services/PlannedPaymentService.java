package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.PlannedPayment;
import com.finanzapp.app_financiera.models.Record;
import com.finanzapp.app_financiera.repository.PlannedPaymentRepository;
import com.finanzapp.app_financiera.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlannedPaymentService {

    private final PlannedPaymentRepository pagoRepository;
    private final RecordRepository recordRepository;

    @Autowired
    public PlannedPaymentService(PlannedPaymentRepository pagoRepository, RecordRepository recordRepository) {
        this.pagoRepository = pagoRepository;
        this.recordRepository = recordRepository;
        initSampleData();
    }

    // Inicialización de datos de ejemplo
    private void initSampleData() {
         LocalDate now = LocalDate.now();
        save(new PlannedPayment(
                "321",
                "Gasto",
                "Vivienda",
                "Pintada de paredes",
                now.plusDays(1),
                100000
        ));

        save(new PlannedPayment(
                "321",
                "Gasto",
                "Transporte",
                "Pasaje de bus",
                now.plusDays(5),
                5000
        ));

        save(new PlannedPayment(
                "321",
                "Ingreso",
                "Salario",
                "Pago mensual",
                now.plusMonths(2),
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

    public List<PlannedPayment> buscarPorFiltros(String userId, String query, String futurePeriod) {
        return pagoRepository.buscarPorFiltros(userId, query, futurePeriod);
    }

    public PlannedPayment update(String id, PlannedPayment pago) {
        findById(id); // Valida que exista

        // Creamos un nuevo objeto con el mismo ID
        pago.setId(id);
        return pagoRepository.update(pago);
    }
    
    public PlannedPayment confirmarPago(String id) {
        PlannedPayment pp = findById(id); // Valida que exista
        
        if (pp.getPaymentDate() != null) throw new ResponseStatusException(HttpStatus.CONFLICT, "Pago con ID " + id + " ya fue confirmado");
        // Creamos un nuevo objeto con el mismo ID
        pp.setPaymentDate(LocalDate.now());
        pagoRepository.update(pp);
        
        recordRepository.save(new Record(pp.getUserId(), pp.getType(), LocalDateTime.now(), pp.getCategory(), pp.getName(), pp.getAmount()));
        return pp;
    }

    public void deleteById(String id) {
        findById(id);
        pagoRepository.deleteById(id);
    }
}
