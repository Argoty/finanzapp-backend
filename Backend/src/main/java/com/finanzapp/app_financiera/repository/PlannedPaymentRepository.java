package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.PlannedPayment;
import org.springframework.stereotype.Repository;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PlannedPaymentRepository {

    private final Map<String, PlannedPayment> tablaPagos = new HashMap<>();

    public PlannedPayment save(PlannedPayment pago) {
        tablaPagos.put(pago.getId(), pago);
        return pago;
    }

    public PlannedPayment findById(String id) {
        return tablaPagos.get(id);
    }

    public List<PlannedPayment> findAll() {
        return new ArrayList<>(tablaPagos.values());
    }

    public void deleteById(String id) {
        tablaPagos.remove(id);
    }

    public PlannedPayment update(PlannedPayment pago) {
        if (tablaPagos.containsKey(pago.getId())) {
            tablaPagos.put(pago.getId(), pago);
            return pago;
        }
        return null;
    }

    public List<PlannedPayment> buscarPorFiltros(String query) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return tablaPagos.values().stream()
                .filter(p -> (query == null || query.isEmpty())
                        || p.getType().toLowerCase().contains(query.toLowerCase())
                        || p.getCategory().toLowerCase().contains(query.toLowerCase())
                        || p.getName().toLowerCase().contains(query.toLowerCase())
                        || p.getDueDate().format(formatter).contains(query)
                        || String.valueOf(p.getAmount()).contains(query))
                .collect(Collectors.toList());
    }
}
