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
    public PlannedPaymentService(PlannedPaymentRepository pagoRepository,
                                 RecordRepository recordRepository) {
        this.pagoRepository = pagoRepository;
        this.recordRepository = recordRepository;
    }

    public PlannedPayment save(PlannedPayment pago) {
        return pagoRepository.save(pago);
    }

    public PlannedPayment findById(int id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pago con ID " + id + " no encontrado"));
    }

    public List<PlannedPayment> buscarPorFiltros(int userId, String query, String futurePeriod) {
        LocalDate limitDate = calcularFechaLimite(futurePeriod);
        return pagoRepository.findByUserIdAndFilters(userId, limitDate, query);
    }

    public PlannedPayment update(int id, PlannedPayment pago) {
        findById(id);
        pago.setId(id);
        return pagoRepository.save(pago);
    }

    public PlannedPayment confirmarPago(int id) {
        PlannedPayment pp = findById(id);
        if (pp.getPaymentDate() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Pago con ID " + id + " ya fue confirmado");
        }
        pp.setPaymentDate(LocalDate.now());
        pagoRepository.save(pp);
        recordRepository.save(new Record(
                pp.getUserId(), pp.getType(), LocalDateTime.now(),
                pp.getCategory(), pp.getName(), pp.getAmount()
        ));
        return pp;
    }

    public void deleteById(int id) {
        findById(id);
        pagoRepository.deleteById(id);
    }

    private LocalDate calcularFechaLimite(String futurePeriod) {
        LocalDate hoy = LocalDate.now();
        if (futurePeriod == null) return null;
        return switch (futurePeriod.toLowerCase()) {
            case "1 semana" -> hoy.plusWeeks(1);
            case "1 mes"    -> hoy.plusMonths(1);
            case "3 meses"  -> hoy.plusMonths(3);
            case "6 meses"  -> hoy.plusMonths(6);
            case "1 año"    -> hoy.plusYears(1);
            default           -> null;
        };
    }
}

